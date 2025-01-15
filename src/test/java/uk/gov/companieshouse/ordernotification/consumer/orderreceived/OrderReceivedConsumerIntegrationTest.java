package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import email.email_send;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.ordernotification.config.TestConfig;
import uk.gov.companieshouse.ordernotification.config.TestEnvironmentSetupHelper;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.orders.OrderReceived;

@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-stubbed.properties")
class OrderReceivedConsumerIntegrationTest {

    private static MockServerContainer container;
    private static int attempt;
    @Autowired
    private KafkaProducer<String, OrderReceived> orderReceivedProducer;
    @Autowired
    private KafkaProducer<String, OrderReceived> orderReceivedRetryProducer;
    @Autowired
    private KafkaConsumer<String, email_send> emailSendConsumer;
    private MockServerClient client;
    private CountDownLatch eventLatch;

    @BeforeAll
    static void before() {
        container = new MockServerContainer(DockerImageName.parse(
                "mockserver/mockserver:5.15.0"));
        container.start();
        TestEnvironmentSetupHelper.setEnvironmentVariable("API_URL",
                "http://" + container.getHost() + ":" + container.getServerPort());
        TestEnvironmentSetupHelper.setEnvironmentVariable("CHS_API_KEY", "123");
        TestEnvironmentSetupHelper.setEnvironmentVariable("PAYMENTS_API_URL",
                "http://" + container.getHost() + ":" + container.getServerPort());
    }

    @AfterAll
    static void after() {
        container.stop();
    }

    @BeforeEach
    void setup() {
        client = new MockServerClient(container.getHost(), container.getServerPort());
        eventLatch = new CountDownLatch(1);
        OrderReceivedKafkaConsumerAspect.setEventLatch(eventLatch);
    }

    @AfterEach
    void teardown() {
        client.reset();
    }

    private static OrderReceived getOrderReceived() {
        OrderReceived orderReceived = new OrderReceived();
        orderReceived.setOrderUri(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        orderReceived.setAttempt(attempt++);
        return orderReceived;
    }

    private static OrderReceived getOrderReceivedRetry() {
        OrderReceived orderReceivedRetry = new OrderReceived();
        orderReceivedRetry.setAttempt(attempt++);
        orderReceivedRetry.setOrderUri(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        return orderReceivedRetry;
    }
}
