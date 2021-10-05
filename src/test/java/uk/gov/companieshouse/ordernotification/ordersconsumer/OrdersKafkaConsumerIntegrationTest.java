package uk.gov.companieshouse.ordernotification.ordersconsumer;

import email.email_send;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@SpringBootTest
@DirtiesContext
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@ActiveProfiles("feature-flags-disabled")
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

    private CountDownLatch eventLatch;

    @BeforeAll
    static void before() {
        container = new MockServerContainer(DockerImageName.parse("jamesdbloom/mockserver:mockserver-5.5.4"));
        container.start();
        TestEnvironmentSetupHelper.setEnvironmentVariable("API_URL", "http://"+container.getHost()+":"+container.getServerPort());
        TestEnvironmentSetupHelper.setEnvironmentVariable("CHS_API_KEY", "123");
        TestEnvironmentSetupHelper.setEnvironmentVariable("PAYMENTS_API_URL", "http://"+container.getHost()+":"+container.getServerPort());
    }

    @BeforeEach
    void setup() {
        client = new MockServerClient(container.getHost(), container.getServerPort());
        eventLatch = new CountDownLatch(1);
        OrdersKafkaConsumer.setEventLatch(eventLatch);
    }

    @AfterEach
    void teardown() {
        client.reset();
    }

    @AfterAll
    static void after() {
        container.stop();
    }

    @Test
    void testHandlesCertificateOrderReceivedMessage() throws ExecutionException, InterruptedException, IOException {
        //given
        client.when(request()
                .withPath(TestConstants.ORDER_NOTIFICATION_REFERENCE)
                .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString("/certified-certificate.json", StandardCharsets.UTF_8))));

        // when
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "email-send");
        orderReceivedProducer.send(new ProducerRecord<>("order-received", "order-received", getOrderReceived())).get();
        eventLatch.await(30, TimeUnit.SECONDS);
        email_send actual = consumer.poll(Duration.ofSeconds(15)).iterator().next().value();

        // then
        assertEquals("order_notification_sender", actual.getAppId());
        assertEquals("order_notification_sender_certificate", actual.getMessageId());
        assertEquals("order_notification_sender_certificate", actual.getMessageType());
        assertEquals("noreply@companieshouse.gov.uk", actual.getEmailAddress());
        assertNotNull(actual.getData());
    }

    @Test
    void testHandlesCertifiedCopyOrderReceivedMessage() throws ExecutionException, InterruptedException, IOException {
        // given
        client.when(request()
                .withPath(TestConstants.ORDER_NOTIFICATION_REFERENCE)
                .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString("/certified-copy.json", StandardCharsets.UTF_8))));

        // when
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "email-send");
        orderReceivedProducer.send(new ProducerRecord<>("order-received", "order-received", getOrderReceived())).get();
        eventLatch.await(30, TimeUnit.SECONDS);
        email_send actual = consumer.poll(Duration.ofSeconds(15)).iterator().next().value();

        // then
        assertEquals("order_notification_sender", actual.getAppId());
        assertEquals("order_notification_sender_document", actual.getMessageId());
        assertEquals("order_notification_sender_document", actual.getMessageType());
        assertEquals("noreply@companieshouse.gov.uk", actual.getEmailAddress());
        assertNotNull(actual.getData());
    }

    @Test
    void testHandlesMissingImageOrderReceivedMessage() throws ExecutionException, InterruptedException, IOException {
        // given
        client.when(request()
                .withPath(TestConstants.ORDER_NOTIFICATION_REFERENCE)
                .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString("/missing-image-delivery.json", StandardCharsets.UTF_8))));

        // when
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "email-send");
        orderReceivedProducer.send(new ProducerRecord<>("order-received", "order-received", getOrderReceived())).get();
        eventLatch.await(30, TimeUnit.SECONDS);
        email_send actual = consumer.poll(Duration.ofSeconds(15)).iterator().next().value();

        // then
        assertEquals("order_notification_sender", actual.getAppId());
        assertEquals("order_notification_sender_missing_image", actual.getMessageId());
        assertEquals("order_notification_sender_missing_image", actual.getMessageType());
        assertEquals("noreply@companieshouse.gov.uk", actual.getEmailAddress());
        assertNotNull(actual.getData());
    }

    @Test
    void testHandlesOrderReceivedRetryMessage() throws ExecutionException, InterruptedException, IOException {
        // given
        client.when(request()
                .withPath(TestConstants.ORDER_NOTIFICATION_REFERENCE)
                .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString("/missing-image-delivery.json", StandardCharsets.UTF_8))));

        // when
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "email-send");
        orderReceivedRetryProducer.send(new ProducerRecord<>("order-received-notification-retry", "order-received-notification-retry", getOrderReceivedNotificationRetry())).get();
        eventLatch.await(30, TimeUnit.SECONDS);
        email_send actual = consumer.poll(Duration.ofSeconds(15)).iterator().next().value();

        // then
        assertEquals("order_notification_sender", actual.getAppId());
        assertEquals("order_notification_sender_missing_image", actual.getMessageId());
        assertEquals("order_notification_sender_missing_image", actual.getMessageType());
        assertEquals("noreply@companieshouse.gov.uk", actual.getEmailAddress());
        assertNotNull(actual.getData());
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
