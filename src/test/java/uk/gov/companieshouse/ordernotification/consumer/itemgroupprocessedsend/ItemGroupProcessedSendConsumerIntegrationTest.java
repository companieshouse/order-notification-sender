package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_GROUP_PROCESSED_SEND;

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
import org.junit.jupiter.api.DisplayName;
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
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.config.TestConfig;
import uk.gov.companieshouse.ordernotification.config.TestEnvironmentSetupHelper;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-stubbed.properties")
class ItemGroupProcessedSendConsumerIntegrationTest {

    private static MockServerContainer container;

    @Autowired
    private KafkaProducer<String, ItemGroupProcessedSend> itemGroupProcessedSendProducer;

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
        ItemGroupProcessedSendKafkaConsumerAspect.setEventLatch(eventLatch);
    }

    @AfterEach
    void teardown() {
        client.reset();
    }

    @Test
    @DisplayName("Handles item-group-processed-send message")
    void testHandlesItemGroupProcessedSendMessage()
        throws ExecutionException, InterruptedException, IOException {

        // given
        client.when(request()
                .withPath(TestConstants.ORDER_NOTIFICATION_REFERENCE)
                .withMethod(HttpMethod.GET.toString()))
            .respond(response()
                .withStatusCode(HttpStatus.OK.value())
                .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .withBody(JsonBody.json(IOUtils.resourceToString(
                    "/fixtures/digital-certified-copy.json",
                    StandardCharsets.UTF_8))));

        // when
        itemGroupProcessedSendProducer.send(new ProducerRecord<>("item-group-processed-send",
                "item-group-processed-send",
            ITEM_GROUP_PROCESSED_SEND)).get();

        // then
        final boolean messageHandled = eventLatch.await(60, TimeUnit.SECONDS);
        if (!messageHandled) {
            fail("FAILED to handle the item-group-processed-send message produced!");
        }

        eventLatch.await(30, TimeUnit.SECONDS);
        email_send actual = emailSendConsumer.poll(Duration.ofSeconds(15))
            .iterator()
            .next()
            .value();

        // then
        assertEquals("order_notification_sender", actual.getAppId());
        assertEquals("digital_item_ready", actual.getMessageId());
        assertEquals("digital_item_ready", actual.getMessageType());
        assertEquals("noreply@companieshouse.gov.uk", actual.getEmailAddress());
        assertNotNull(actual.getData());

    }

}
