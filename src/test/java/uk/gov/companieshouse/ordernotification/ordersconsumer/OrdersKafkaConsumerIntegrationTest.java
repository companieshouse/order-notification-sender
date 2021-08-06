package uk.gov.companieshouse.ordernotification.ordersconsumer;

import email.email_send;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-stubbed.properties")
class OrdersKafkaConsumerIntegrationTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private OrdersKafkaConsumer ordersKafkaConsumer;

    @Autowired
    private KafkaProducer<String, OrderReceived> orderReceivedProducer;

    @Autowired
    private KafkaProducer<String, OrderReceivedNotificationRetry> orderReceivedRetryProducer;

    @Autowired
    private KafkaConsumer<String, email_send> consumer;

    private MockServerClient client;

    private static MockServerContainer container;

    @BeforeAll
    static void before() {
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
    }

    @AfterAll
    static void after() {
        container.stop();
    }

    @Test
    void testHandlesOrderReceivedMessage() throws ExecutionException, InterruptedException {
        // when
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "email-send");
        orderReceivedProducer.send(new ProducerRecord<>("order-received", "order-received", getOrderReceived())).get();

        // then
        assertEquals(1, consumer.poll(Duration.ofSeconds(10)).count());
    }

    @Test
    void testHandlesOrderReceivedRetryMessage() throws ExecutionException, InterruptedException {
        // when
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "email-send");
        orderReceivedRetryProducer.send(new ProducerRecord<>("order-received-notification-retry", "order-received-notification-retry", getOrderReceivedNotificationRetry())).get();

        // then
        assertEquals(1, consumer.poll(Duration.ofSeconds(10)).count());
    }

    private static OrderReceived getOrderReceived() {
        OrderReceived orderReceived = new OrderReceived();
        orderReceived.setOrderUri(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        return orderReceived;
    }

    private static OrderReceivedNotificationRetry getOrderReceivedNotificationRetry() {
        OrderReceivedNotificationRetry orderReceivedNotificationRetry = new OrderReceivedNotificationRetry();
        orderReceivedNotificationRetry.setAttempt(2);
        orderReceivedNotificationRetry.setOrder(new OrderReceived(TestConstants.ORDER_NOTIFICATION_REFERENCE));
        return orderReceivedNotificationRetry;
    }
}
