package uk.gov.companieshouse.ordernotification.ordersconsumer;

import email.email_send;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.ordernotification.config.TestConfig;
import uk.gov.companieshouse.ordernotification.config.TestEnvironmentSetupHelper;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.orders.OrderReceived;
import uk.gov.companieshouse.orders.OrderReceivedNotificationRetry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@DirtiesContext
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-stubbed-error-consumer.properties")
@ActiveProfiles("feature-flags-disabled")
class OrdersKafkaErrorConsumerIntegrationTest {
    @Autowired
    private OrdersKafkaConsumer ordersKafkaConsumer;

    @Autowired
    private KafkaProducer<String, OrderReceived> orderReceivedProducer;

    @Autowired
    private KafkaProducer<String, OrderReceivedNotificationRetry> orderReceivedRetryProducer;

    @Autowired
    private KafkaConsumer<String, email_send> consumer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private MockServerClient client;

    private static MockServerContainer container;

    private static final CountDownLatch latch = new CountDownLatch(1);
    private CountDownLatch eventLatch;


    @BeforeAll
    static void before() {
        OrdersKafkaConsumer.setStartupLatch(latch);
        container = new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.5.4"));
        container.start();
        TestEnvironmentSetupHelper.setEnvironmentVariable("API_URL", "http://"+container.getHost()+":"+container.getServerPort());
        TestEnvironmentSetupHelper.setEnvironmentVariable("CHS_API_KEY", "123");
        TestEnvironmentSetupHelper.setEnvironmentVariable("PAYMENTS_API_URL", "http://"+container.getHost()+":"+container.getServerPort());
    }

    @BeforeEach
    void setup() throws IOException {
        client = new MockServerClient(container.getHost(), container.getServerPort());
        client.when(request()
                .withPath(TestConstants.ORDER_NOTIFICATION_REFERENCE)
                .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString("/missing-image-delivery.json", StandardCharsets.UTF_8))));
        eventLatch = new CountDownLatch(1);
        OrdersKafkaConsumer.setEventLatch(eventLatch);
    }

    @AfterAll
    static void after() {
        container.stop();
        OrdersKafkaConsumer.setStartupLatch(new CountDownLatch(0));
    }

    @Test
    void testHandlesOrderReceivedErrorMessage() throws ExecutionException, InterruptedException {
        //when {the application is initialised}
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "email-send");
        orderReceivedProducer.send(new ProducerRecord<>("order-received-notification-error", "order-received-notification-error", getOrderReceived())).get();
        latch.countDown();
        eventLatch.await(30, TimeUnit.SECONDS);

        // then {messages up to the latest offset in the error topic at startup should be processed}
        assertEquals(1, consumer.poll(Duration.ofSeconds(15)).count());

        // and {subsequent messages should not be processed}
        eventLatch = new CountDownLatch(1);
        OrdersKafkaConsumer.setEventLatch(eventLatch);
        orderReceivedProducer.send(new ProducerRecord<>("order-received-notification-error", "order-received-notification-error", getOrderReceived())).get();
        eventLatch.await(30, TimeUnit.SECONDS);
        assertEquals(0, consumer.poll(Duration.ofSeconds(15)).count());
    }

    private static OrderReceived getOrderReceived() {
        OrderReceived orderReceived = new OrderReceived();
        orderReceived.setOrderUri(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        return orderReceived;
    }
}
