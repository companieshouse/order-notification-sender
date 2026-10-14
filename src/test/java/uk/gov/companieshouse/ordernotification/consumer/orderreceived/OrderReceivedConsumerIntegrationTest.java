
package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.apache.commons.io.IOUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.ordernotification.config.KafkaConfig;
import uk.gov.companieshouse.ordernotification.config.KafkaTopics;
import uk.gov.companieshouse.ordernotification.config.TestConfig;
import uk.gov.companieshouse.orders.OrderReceived;

@SpringBootTest
@Import({KafkaConfig.class, TestConfig.class})
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@WireMockTest(httpPort = 8523)
class OrderReceivedConsumerIntegrationTest {

    private static int orderId = 123456;

    @Autowired
    private KafkaProducer<String, OrderReceived> orderReceivedProducer;

    @Autowired
    private OrderReceivedKafkaConsumerAspect orderReceivedConsumerAspect;

    @Autowired
    private KafkaTopics kafkaTopics;

    @AfterEach
    void teardown() {
        WireMock.reset();
        ++orderId;
    }

    @ParameterizedTest
    @MethodSource("orderReceivedFixturesProvider")
    void testConsumesOrderReceivedFromMainTopic(String fixtureFile) throws ExecutionException, InterruptedException, IOException {
        // given
        WireMock.stubFor(get(urlEqualTo(getOrderReference()))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                        .withBody(IOUtils.resourceToString(
                                fixtureFile,
                                StandardCharsets.UTF_8))));
        WireMock.stubFor(post("/send-email")
                .willReturn(aResponse()
                        .withStatus(HttpStatus.OK.value())));
        orderReceivedConsumerAspect.setEventLatch(new CountDownLatch(1));

        // when
        ProducerRecord<String, OrderReceived> producerRecord = new ProducerRecord<>(
                kafkaTopics.getOrderReceived(),
                kafkaTopics.getOrderReceived(),
                getOrderReceived());
        orderReceivedProducer.send(producerRecord).get();

        // then
        assertTrue(orderReceivedConsumerAspect.getEventLatch().await(30, TimeUnit.SECONDS));
    }

    private static Stream<String> orderReceivedFixturesProvider() {
        return Stream.of(
                "/fixtures/certified-certificate.json",
                "/fixtures/dissolved-certificate.json",
                "/fixtures/certified-copy.json",
                "/fixtures/missing-image-delivery.json"
        );
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