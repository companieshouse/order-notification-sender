package uk.gov.companieshouse.ordernotification.ordersconsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import email.email_send;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
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
class OrdersKafkaConsumerIntegrationTest {

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
                "jamesdbloom/mockserver:mockserver-5.5.4"));
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
        OrdersKafkaConsumerAspect.setEventLatch(eventLatch);
    }

    @AfterEach
    void teardown() {
        client.reset();
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
                        .withBody(JsonBody.json(IOUtils.resourceToString(
                                "/fixtures/certified-certificate.json",
                                StandardCharsets.UTF_8))));

        // when
        orderReceivedProducer.send(new ProducerRecord<>("order-received",
                "order-received",
                getOrderReceived())).get();
        eventLatch.await(30, TimeUnit.SECONDS);
        email_send actual = emailSendConsumer.poll(Duration.ofSeconds(15))
                .iterator()
                .next()
                .value();

        // then
        assertEquals("order_notification_sender", actual.getAppId());
        assertEquals("order_notification_sender_certificate", actual.getMessageId());
        assertEquals("order_notification_sender_certificate", actual.getMessageType());
        assertEquals("noreply@companieshouse.gov.uk", actual.getEmailAddress());
        assertNotNull(actual.getData());
    }

    @Test
    void testHandlesDissolvedCertificateOrderReceivedMessage() throws ExecutionException, InterruptedException, IOException {
        //given
        client.when(request()
                        .withPath(TestConstants.ORDER_NOTIFICATION_REFERENCE)
                        .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString(
                                "/fixtures/dissolved-certificate.json",
                                StandardCharsets.UTF_8))));

        // when
        orderReceivedProducer.send(new ProducerRecord<>("order-received",
                "order-received",
                getOrderReceived())).get();
        eventLatch.await(30, TimeUnit.SECONDS);
        email_send actual = emailSendConsumer.poll(Duration.ofSeconds(15))
                .iterator()
                .next()
                .value();

        // then
        assertEquals("order_notification_sender", actual.getAppId());
        assertEquals("order_notification_sender_dissolved_certificate", actual.getMessageId());
        assertEquals("order_notification_sender_dissolved_certificate", actual.getMessageType());
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
                        .withBody(JsonBody.json(IOUtils.resourceToString("/fixtures/certified-copy.json",
                                StandardCharsets.UTF_8))));

        // when
        orderReceivedProducer.send(new ProducerRecord<>("order-received",
                "order-received",
                getOrderReceived())).get();
        eventLatch.await(30, TimeUnit.SECONDS);
        email_send actual = emailSendConsumer.poll(Duration.ofSeconds(15))
                .iterator()
                .next()
                .value();

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
                        .withBody(JsonBody.json(IOUtils.resourceToString(
                                "/fixtures/missing-image-delivery.json",
                                StandardCharsets.UTF_8))));

        // when
        orderReceivedProducer.send(new ProducerRecord<>("order-received",
                "order-received",
                getOrderReceived())).get();
        eventLatch.await(30, TimeUnit.SECONDS);
        email_send actual = emailSendConsumer.poll(Duration.ofSeconds(15))
                .iterator()
                .next()
                .value();

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
                        .withBody(JsonBody.json(IOUtils.resourceToString(
                                "/fixtures/missing-image-delivery.json",
                                StandardCharsets.UTF_8))));

        // when
        orderReceivedRetryProducer.send(new ProducerRecord<>("order-received-notification-retry",
                "order-received-notification-retry",
                getOrderReceivedRetry())).get();
        eventLatch.await(30, TimeUnit.SECONDS);
        email_send actual = emailSendConsumer.poll(Duration.ofSeconds(15))
                .iterator()
                .next()
                .value();

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
