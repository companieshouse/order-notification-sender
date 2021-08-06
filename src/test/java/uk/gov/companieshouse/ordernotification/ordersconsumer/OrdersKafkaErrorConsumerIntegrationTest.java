package uk.gov.companieshouse.ordernotification.ordersconsumer;

import email.email_send;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@DirtiesContext
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-stubbed-error-consumer.properties")
public class OrdersKafkaErrorConsumerIntegrationTest {
    @Autowired
    private OrdersKafkaConsumer ordersKafkaConsumer;

    @Autowired
    private KafkaProducer<String, OrderReceived> orderReceivedProducer;

    @Autowired
    private KafkaProducer<String, OrderReceivedNotificationRetry> orderReceivedRetryProducer;

    @Autowired
    private KafkaConsumer<String, email_send> consumer;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapUrls;

    private MockServerClient client;

    private static MockServerContainer container;
    private static KafkaContainer kafkaContainer;

    private static CountDownLatch latch = new CountDownLatch(1);

    @BeforeAll
    static void before() throws ExecutionException, InterruptedException {
        OrdersKafkaConsumer.setLatch(latch);
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
        kafkaContainer.start();
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
        container = new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.5.4"));
        container.start();
        Admin client = Admin.create(Collections.singletonMap(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()));
        client.createTopics(Arrays.asList(
                new NewTopic("order-received", 1, (short)1),
                new NewTopic("order-received-notification-retry", 1, (short)1),
                new NewTopic("order-received-notification-error", 1, (short)1),
                new NewTopic("email-send", 1, (short)1))).all().get();
        TestEnvironmentSetupHelper.setEnvironmentVariable("API_URL", "http://"+container.getHost()+":"+container.getServerPort());
        TestEnvironmentSetupHelper.setEnvironmentVariable("CHS_API_KEY", "123");
        TestEnvironmentSetupHelper.setEnvironmentVariable("PAYMENTS_API_URL", "http://"+container.getHost()+":"+container.getServerPort());
    }

    @BeforeEach
    void setup() throws IOException, ExecutionException, InterruptedException {
        client = new MockServerClient(container.getHost(), container.getServerPort());
        client.when(request()
                .withPath(TestConstants.ORDER_NOTIFICATION_REFERENCE)
                .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString("/missing-image-delivery.json", StandardCharsets.UTF_8))));
        consumer.assign(Collections.singletonList(new TopicPartition("email-send", 0)));
        orderReceivedProducer.send(new ProducerRecord<>("order-received-notification-error", "", getOrderReceived())).get();
    }

    @AfterAll
    static void after() {
        kafkaContainer.stop();
        container.stop();
        OrdersKafkaConsumer.setLatch(new CountDownLatch(0));
    }

    @Test
    void testHandlesOrderReceivedErrorMessage() throws ExecutionException, InterruptedException {
        //when {the application is initialised}
        latch.countDown();

        // then {messages up to the latest offset in the error topic at startup should be processed}
        ConsumerRecords<String, email_send> results = consumer.poll(Duration.ofSeconds(5));
        assertEquals(1, results.count());

        // and {subsequent messages should not be processed}
        orderReceivedProducer.send(new ProducerRecord<>("order-received-notification-error", "", getOrderReceived())).get();
        results = consumer.poll(Duration.ofSeconds(5));
        assertEquals(0, results.count());
    }

    private static OrderReceived getOrderReceived() {
        OrderReceived orderReceived = new OrderReceived();
        orderReceived.setOrderUri(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        return orderReceived;
    }
}
