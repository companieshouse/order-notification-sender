package uk.gov.companieshouse.ordernotification.ordersconsumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import email.email_send;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.io.IOUtils;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.ordernotification.config.KafkaTopics;
import uk.gov.companieshouse.ordernotification.config.TestEnvironmentSetupHelper;
import uk.gov.companieshouse.orders.OrderReceived;

@SpringBootTest
@Import(KafkaConfig.class)
@TestPropertySource(locations = "classpath:application-stubbed.properties",
        properties = {"uk.gov.companieshouse.order-notification-sender.error-consumer=true"})
@ActiveProfiles("feature-flags-enabled")
class OrderMessageErrorConsumerIntegrationTest {

    private static int orderId = 123456;
    private static MockServerContainer container;
    private MockServerClient client;

    @Autowired
    private KafkaConsumer<String, email_send> emailSendConsumer;

    @Autowired
    private KafkaProducer<String, OrderReceived> orderReceivedProducer;

    @Autowired
    private KafkaConsumer<String, OrderReceived> orderReceivedRetryConsumer;

    @Autowired
    private OrderMessageErrorConsumerAspect orderMessageErrorConsumerAspect;

    @Autowired
    private KafkaTopics kafkaTopics;

    @Autowired
    private PartitionOffset errorRecoveryOffset;

    @Autowired
    private ErrorConsumerController errorConsumerController;

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
        errorRecoveryOffset.reset();
        errorConsumerController.resumeConsumerThread();
    }

    @AfterEach
    void teardown() {
        client.reset();
        ++orderId;
    }

    @Test
    void testConsumesCertificateOrderReceivedFromErrorTopic() throws
            ExecutionException, InterruptedException,
            IOException {
        //given
        client.when(request()
                        .withPath(getOrderReference())
                        .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString(
                                "/fixtures/certified-certificate.json",
                                StandardCharsets.UTF_8))));
        orderMessageErrorConsumerAspect.setBeforeProcessOrderReceivedEventLatch(new CountDownLatch(1));
        orderMessageErrorConsumerAspect.setAfterOrderConsumedEventLatch(new CountDownLatch(1));

        //when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceivedError(),
                kafkaTopics.getOrderReceivedError(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();
        orderMessageErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().countDown();
        orderMessageErrorConsumerAspect.getAfterOrderConsumedEventLatch().await(30, TimeUnit.SECONDS);
        email_send actual = KafkaTestUtils.getSingleRecord(emailSendConsumer, kafkaTopics.getEmailSend()).value();

        //then
        assertEquals(0, orderMessageErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().getCount());
        assertEquals(0, orderMessageErrorConsumerAspect.getAfterOrderConsumedEventLatch().getCount());
        assertEquals("order_notification_sender",
                actual.getAppId());
        assertEquals("order_notification_sender_certificate",
                actual.getMessageId());
        assertEquals("order_notification_sender_certificate",
                actual.getMessageType());
        assertEquals("noreply@companieshouse.gov.uk", actual.getEmailAddress());
        assertTrue(actual.getData().contains("demo@ch.gov.uk")); // verify recipient email address
    }

    @Test
    void testConsumesDissolvedCertificateOrderReceivedFromErrorTopic() throws
            ExecutionException, InterruptedException,
            IOException {
        //given
        client.when(request()
                        .withPath(getOrderReference())
                        .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString(
                                "/fixtures/dissolved-certificate.json",
                                StandardCharsets.UTF_8))));
        orderMessageErrorConsumerAspect.setBeforeProcessOrderReceivedEventLatch(new CountDownLatch(1));
        orderMessageErrorConsumerAspect.setAfterOrderConsumedEventLatch(new CountDownLatch(1));

        //when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceivedError(),
                kafkaTopics.getOrderReceivedError(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();
        orderMessageErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().countDown();
        orderMessageErrorConsumerAspect.getAfterOrderConsumedEventLatch().await(30, TimeUnit.SECONDS);
        email_send actual = KafkaTestUtils.getSingleRecord(emailSendConsumer, kafkaTopics.getEmailSend()).value();

        //then
        assertEquals(0, orderMessageErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().getCount());
        assertEquals(0, orderMessageErrorConsumerAspect.getAfterOrderConsumedEventLatch().getCount());
        assertEquals("order_notification_sender",
                actual.getAppId());
        assertEquals("order_notification_sender_dissolved_certificate",
                actual.getMessageId());
        assertEquals("order_notification_sender_dissolved_certificate",
                actual.getMessageType());
        assertEquals("noreply@companieshouse.gov.uk", actual.getEmailAddress());
        assertTrue(actual.getData().contains("demo@ch.gov.uk")); // verify recipient email address
    }

    @Test
    void testConsumesCertifiedDocumentOrderReceivedFromErrorTopic() throws ExecutionException, InterruptedException, IOException {
        //given
        client.when(request()
                        .withPath(getOrderReference())
                        .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString(
                                "/fixtures/certified-copy.json",
                                StandardCharsets.UTF_8))));
        orderMessageErrorConsumerAspect.setBeforeProcessOrderReceivedEventLatch(new CountDownLatch(1));
        orderMessageErrorConsumerAspect.setAfterOrderConsumedEventLatch(new CountDownLatch(1));

        //when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceivedError(),
                kafkaTopics.getOrderReceivedError(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();
        orderMessageErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().countDown();
        orderMessageErrorConsumerAspect.getAfterOrderConsumedEventLatch().await(30, TimeUnit.SECONDS);
        email_send actual = KafkaTestUtils.getSingleRecord(emailSendConsumer, kafkaTopics.getEmailSend()).value();

        //then
        assertEquals(0, orderMessageErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().getCount());
        assertEquals(0, orderMessageErrorConsumerAspect.getAfterOrderConsumedEventLatch().getCount());
        assertEquals("order_notification_sender", actual.getAppId());
        assertEquals("order_notification_sender_document", actual.getMessageId());
        assertEquals("order_notification_sender_document", actual.getMessageType());
        assertEquals("noreply@companieshouse.gov.uk", actual.getEmailAddress());
        assertTrue(actual.getData().contains("demo@ch.gov.uk")); // verify recipient email address
    }

    @Test
    void testConsumesMissingImageDeliveryFromNotificationErrorAndPublishesToEmailSend() throws ExecutionException, InterruptedException, IOException {
        //given
        client.when(request()
                        .withPath(getOrderReference())
                        .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(JsonBody.json(IOUtils.resourceToString(
                                "/fixtures/missing-image-delivery.json",
                                StandardCharsets.UTF_8))));
        orderMessageErrorConsumerAspect.setBeforeProcessOrderReceivedEventLatch(new CountDownLatch(1));
        orderMessageErrorConsumerAspect.setAfterOrderConsumedEventLatch(new CountDownLatch(1));

        //when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceivedError(),
                kafkaTopics.getOrderReceivedError(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();
        orderMessageErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().countDown();
        orderMessageErrorConsumerAspect.getAfterOrderConsumedEventLatch().await(30, TimeUnit.SECONDS);
        email_send actual = KafkaTestUtils.getSingleRecord(emailSendConsumer,
                kafkaTopics.getEmailSend()).value();

        //then
        assertEquals(0, orderMessageErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().getCount());
        assertEquals(0, orderMessageErrorConsumerAspect.getAfterOrderConsumedEventLatch().getCount());
        assertEquals("order_notification_sender", actual.getAppId());
        assertEquals("order_notification_sender_missing_image", actual.getMessageId());
        assertEquals("order_notification_sender_missing_image", actual.getMessageType());
        assertEquals("noreply@companieshouse.gov.uk", actual.getEmailAddress());
        assertTrue(actual.getData().contains("demo@ch.gov.uk")); // verify recipient email address
    }

    @Test
    void testPublishesOrderReceivedToRetryTopicWhenOrdersApiIsUnavailable() throws ExecutionException, InterruptedException {
        //given
        client.when(request()
                        .withPath(getOrderReference())
                        .withMethod(HttpMethod.GET.toString()))
                .respond(response()
                        .withStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        orderMessageErrorConsumerAspect.setAfterOrderConsumedEventLatch(new CountDownLatch(1));

        // when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceivedError(),
                kafkaTopics.getOrderReceivedError(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();
        orderMessageErrorConsumerAspect.getAfterOrderConsumedEventLatch().await(30, TimeUnit.SECONDS);

        // Get order received from retry topic
        OrderReceived actual = KafkaTestUtils.getSingleRecord(orderReceivedRetryConsumer, kafkaTopics.getOrderReceivedRetry(), 30000).value();

        // then
        assertEquals(0, orderMessageErrorConsumerAspect.getAfterOrderConsumedEventLatch().getCount());
        assertNotNull(actual);
        assertEquals(getOrderReference(), actual.getOrderUri());
    }

    private OrderReceived getOrderReceived() {
        OrderReceived orderReceived = new OrderReceived();
        orderReceived.setOrderUri(getOrderReference());
        return orderReceived;
    }

    private String getOrderReference() {
        return "/orders/ORD-111111-" + orderId;
    }
}