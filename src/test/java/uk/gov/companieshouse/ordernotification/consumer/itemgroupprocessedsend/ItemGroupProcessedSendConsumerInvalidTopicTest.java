package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static uk.gov.companieshouse.ordernotification.TestUtils.noOfRecordsForTopic;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_GROUP_PROCESSED_SEND;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.SAME_PARTITION_KEY;

import consumer.deserialization.AvroDeserializer;
import consumer.serialization.AvroSerializer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
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
@TestPropertySource(locations = "classpath:application-test_main_nonretryable.yml")
@Import(ItemGroupProcessedSendTestConfig.class)
@ActiveProfiles("test_main_nonretryable")
class ItemGroupProcessedSendConsumerInvalidTopicTest {

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaConsumer<String, ItemGroupProcessedSend> testConsumer;

    @Autowired
    private KafkaProducer<String, ItemGroupProcessedSend> itemGroupProcessedSendProducer;

    @Autowired
    @Value("${kafka.topics.item-group-processed-send}")
    private String mainTopicName;

    @Test
    void testPublishToInvalidMessageTopicIfInvalidDataDeserialised()
        throws InterruptedException, ExecutionException {
        //given
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(testConsumer);

        //when
        Future<RecordMetadata> future =
            itemGroupProcessedSendProducer.send(new ProducerRecord<>(
                mainTopicName,
                0,
                System.currentTimeMillis(),
                SAME_PARTITION_KEY,
                ITEM_GROUP_PROCESSED_SEND));
        future.get();
        ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, 30000L, 2);

        //then
        assertThat(noOfRecordsForTopic(consumerRecords, mainTopicName), is(1));
        assertThat(noOfRecordsForTopic(consumerRecords, mainTopicName + "-retry"), is(0));
        assertThat(noOfRecordsForTopic(consumerRecords, mainTopicName + "-error"), is(0));
        assertThat(noOfRecordsForTopic(consumerRecords, mainTopicName + "-invalid"), is(1));
    }
}
