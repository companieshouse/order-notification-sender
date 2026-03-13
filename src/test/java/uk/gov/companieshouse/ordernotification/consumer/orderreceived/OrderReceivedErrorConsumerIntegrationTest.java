
package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import com.github.tomakehurst.wiremock.WireMockServer;

import uk.gov.companieshouse.ordernotification.config.KafkaConfig;
import uk.gov.companieshouse.ordernotification.config.KafkaTopics;
import uk.gov.companieshouse.ordernotification.config.TestConfig;
import uk.gov.companieshouse.ordernotification.config.TestEnvironmentSetupHelper;
import uk.gov.companieshouse.ordernotification.consumer.PartitionOffset;
import uk.gov.companieshouse.orders.OrderReceived;

@SpringBootTest
@Import({KafkaConfig.class, TestConfig.class})
@TestPropertySource(locations = "classpath:application-stubbed.properties",
        properties = {"uk.gov.companieshouse.order-notification-sender.error-consumer=true"})

class OrderReceivedErrorConsumerIntegrationTest {
        private static int orderId = 123456;
        private static WireMockServer wireMockServer;

    @Autowired
    private KafkaProducer<String, OrderReceived> orderReceivedProducer;

    @Autowired
    private OrderReceivedErrorConsumerAspect orderReceivedErrorConsumerAspect;

    @Autowired
    private KafkaTopics kafkaTopics;

    @Autowired
    private PartitionOffset errorRecoveryOffset;

    @Autowired
    private ErrorConsumerController errorConsumerController;

    @BeforeAll
    static void before() {
        wireMockServer = new WireMockServer(8080); // Use 0 for random port if needed
        wireMockServer.start();
        TestEnvironmentSetupHelper.setEnvironmentVariable("API_URL",
                "http://localhost:" + wireMockServer.port());
        TestEnvironmentSetupHelper.setEnvironmentVariable("CHS_API_KEY", "123");
        TestEnvironmentSetupHelper.setEnvironmentVariable("PAYMENTS_API_URL",
                "http://localhost:" + wireMockServer.port());
    }

        @AfterAll
        static void after() {
                wireMockServer.stop();
        }

        @BeforeEach
        void setup() {
                errorRecoveryOffset.reset();
                errorConsumerController.resumeConsumerThread();
                wireMockServer.resetAll();
        }

        @AfterEach
        void teardown() {
                wireMockServer.resetAll();
                ++orderId;
        }

    @Test
    void testConsumesCertificateOrderReceivedFromErrorTopic() throws
            ExecutionException, InterruptedException,
            IOException {
        //given
        wireMockServer.stubFor(get(urlEqualTo(getOrderReference()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(IOUtils.resourceToString(
                                "/fixtures/certified-certificate.json",
                                StandardCharsets.UTF_8))));
        orderReceivedErrorConsumerAspect.setBeforeProcessOrderReceivedEventLatch(new CountDownLatch(1));
        orderReceivedErrorConsumerAspect.setAfterOrderConsumedEventLatch(new CountDownLatch(1));

        //when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceivedError(),
                kafkaTopics.getOrderReceivedError(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();
        orderReceivedErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().countDown();
        orderReceivedErrorConsumerAspect.getAfterOrderConsumedEventLatch().await(30, TimeUnit.SECONDS);

        //then
        assertEquals(0, orderReceivedErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().getCount());
        assertEquals(0, orderReceivedErrorConsumerAspect.getAfterOrderConsumedEventLatch().getCount());
    }

    @Test
    void testConsumesDissolvedCertificateOrderReceivedFromErrorTopic() throws
            ExecutionException, InterruptedException,
            IOException {
        //given
        wireMockServer.stubFor(get(urlEqualTo(getOrderReference()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(IOUtils.resourceToString(
                                "/fixtures/dissolved-certificate.json",
                                StandardCharsets.UTF_8))));
        orderReceivedErrorConsumerAspect.setBeforeProcessOrderReceivedEventLatch(new CountDownLatch(1));
        orderReceivedErrorConsumerAspect.setAfterOrderConsumedEventLatch(new CountDownLatch(1));

        //when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceivedError(),
                kafkaTopics.getOrderReceivedError(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();
        orderReceivedErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().countDown();
        orderReceivedErrorConsumerAspect.getAfterOrderConsumedEventLatch().await(30, TimeUnit.SECONDS);

        //then
        assertEquals(0, orderReceivedErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().getCount());
        assertEquals(0, orderReceivedErrorConsumerAspect.getAfterOrderConsumedEventLatch().getCount());
    }

    @Test
    void testConsumesCertifiedDocumentOrderReceivedFromErrorTopic() throws ExecutionException, InterruptedException, IOException {
        //given
        wireMockServer.stubFor(get(urlEqualTo(getOrderReference()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(IOUtils.resourceToString(
                                "/fixtures/certified-copy.json",
                                StandardCharsets.UTF_8))));
        orderReceivedErrorConsumerAspect.setBeforeProcessOrderReceivedEventLatch(new CountDownLatch(1));
        orderReceivedErrorConsumerAspect.setAfterOrderConsumedEventLatch(new CountDownLatch(1));

        //when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceivedError(),
                kafkaTopics.getOrderReceivedError(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();
        orderReceivedErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().countDown();
        orderReceivedErrorConsumerAspect.getAfterOrderConsumedEventLatch().await(30, TimeUnit.SECONDS);

        //then
        assertEquals(0, orderReceivedErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().getCount());
        assertEquals(0, orderReceivedErrorConsumerAspect.getAfterOrderConsumedEventLatch().getCount());
    }

    @Test
    void testConsumesMissingImageDeliveryFromNotificationErrorAndPublishesToEmailSend() throws ExecutionException, InterruptedException, IOException {
        //given
        wireMockServer.stubFor(get(urlEqualTo(getOrderReference()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(IOUtils.resourceToString(
                                "/fixtures/missing-image-delivery.json",
                                StandardCharsets.UTF_8))));
        orderReceivedErrorConsumerAspect.setBeforeProcessOrderReceivedEventLatch(new CountDownLatch(1));
        orderReceivedErrorConsumerAspect.setAfterOrderConsumedEventLatch(new CountDownLatch(1));

        //when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceivedError(),
                kafkaTopics.getOrderReceivedError(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();
        orderReceivedErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().countDown();
        orderReceivedErrorConsumerAspect.getAfterOrderConsumedEventLatch().await(30, TimeUnit.SECONDS);

        //then
        assertEquals(0, orderReceivedErrorConsumerAspect.getBeforeProcessOrderReceivedEventLatch().getCount());
        assertEquals(0, orderReceivedErrorConsumerAspect.getAfterOrderConsumedEventLatch().getCount());
    }

    @Test
    void testPublishesOrderReceivedToRetryTopicWhenOrdersApiIsUnavailable() throws ExecutionException, InterruptedException {
        //given
        wireMockServer.stubFor(get(urlEqualTo(getOrderReference()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())));
        orderReceivedErrorConsumerAspect.setAfterOrderConsumedEventLatch(new CountDownLatch(1));

        // when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceivedError(),
                kafkaTopics.getOrderReceivedError(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();
        orderReceivedErrorConsumerAspect.getAfterOrderConsumedEventLatch().await(30, TimeUnit.SECONDS);
        // then
        assertEquals(0, orderReceivedErrorConsumerAspect.getAfterOrderConsumedEventLatch().getCount());
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