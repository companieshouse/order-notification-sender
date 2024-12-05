package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static uk.gov.companieshouse.ordernotification.TestUtils.noOfRecordsForTopic;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_GROUP_PROCESSED_SEND;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.SAME_PARTITION_KEY;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.config.ItemGroupProcessedSendTestConfig;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@EmbeddedKafka(
    topics = {"${kafka.topics.item-group-processed-send}",
        "${kafka.topics.item-group-processed-send}-retry",
        "${kafka.topics.item-group-processed-send}-error",
        "${kafka.topics.item-group-processed-send}-invalid"},
    controlledShutdown = true,
    partitions = 1
)
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@Import(ItemGroupProcessedSendTestConfig.class)
class ItemGroupProcessedSendConsumerPositiveTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaConsumer<String, ItemGroupProcessedSend> testConsumer;

    @Autowired
    private KafkaProducer<String, ItemGroupProcessedSend> testProducer;

    @Autowired
    private CountDownLatch latch;

    @Autowired
    @Value("${kafka.topics.item-group-processed-send}")
    private String mainTopicName;

    @MockBean
    private ItemGroupProcessedSendHandler itemGroupProcessedSendHandler;

    @Captor
    private ArgumentCaptor<Message<ItemGroupProcessedSend>> messageCaptor;

    @Test
    void testConsumeFromMainTopic() throws InterruptedException {
        //given
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(testConsumer);

        //when
        testProducer.send(new ProducerRecord<>(
            mainTopicName, 0, System.currentTimeMillis(), SAME_PARTITION_KEY,
            ITEM_GROUP_PROCESSED_SEND));
        if (!latch.await(30L, TimeUnit.SECONDS)) {
            fail("Timed out waiting for latch");
        }

        //then
        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofMillis(10000L), 1);
        assertThat(noOfRecordsForTopic(consumerRecords, mainTopicName), is(1));
        assertThat(noOfRecordsForTopic(consumerRecords, mainTopicName + "-retry"), is(0));
        assertThat(noOfRecordsForTopic(consumerRecords, mainTopicName + "-error"), is(0));
        assertThat(noOfRecordsForTopic(consumerRecords, mainTopicName + "-invalid"), is(0));
        verify(itemGroupProcessedSendHandler).handleMessage(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getPayload(), is(ITEM_GROUP_PROCESSED_SEND));
    }
}
