package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static org.junit.Assert.fail;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.DIGITAL_DOCUMENT_LOCATION;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.GROUP_ITEM;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_ID;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ORDER_REFERENCE_NUMBER;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.STATUS;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.itemgroupprocessedsend.Item;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.config.TestConfig;
import uk.gov.companieshouse.ordernotification.config.TestEnvironmentSetupHelper;

@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-stubbed.properties")
class ItemGroupProcessedSendConsumerIntegrationTest {

    private static MockServerContainer container;

    @Autowired
    private KafkaProducer<String, ItemGroupProcessedSend> itemGroupProcessedSendProducer;
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
    void testHandlesItemGroupProcessedSendMessage() throws ExecutionException, InterruptedException, IOException {

        // when
        itemGroupProcessedSendProducer.send(new ProducerRecord<>("item-group-processed-send",
                "item-group-processed-send",
            getItemGroupProcessedSend())).get();

        // then
        final boolean messageHandled = eventLatch.await(30, TimeUnit.SECONDS);
        if (!messageHandled) {
            fail("FAILED to handle the item-group-processed-send message produced!");
        }
    }

    private static ItemGroupProcessedSend getItemGroupProcessedSend() {
        final ItemGroupProcessedSend message = new ItemGroupProcessedSend();
        message.setOrderNumber(ORDER_REFERENCE_NUMBER);
        message.setGroupItem(GROUP_ITEM);
        final Item item = new Item();
        item.setId(ITEM_ID);
        item.setStatus(STATUS);
        item.setDigitalDocumentLocation(DIGITAL_DOCUMENT_LOCATION);
        message.setItem(item);
        return message;
    }

}
