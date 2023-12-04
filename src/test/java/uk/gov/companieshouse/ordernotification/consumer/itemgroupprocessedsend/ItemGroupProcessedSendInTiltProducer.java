package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_GROUP_PROCESSED_SEND;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.SAME_PARTITION_KEY;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * "Test" class re-purposed to produce
 * {@link uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend} messages to the
 * <code>item-group-processed-send</code> topic in Tilt. This is NOT to be run as part of an
 * automated test suite. It is for manual testing only.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:item-group-processed-send-in-tilt.properties")
@Import(ItemGroupProcessedSendInTiltProducer.Config.class)
@SuppressWarnings("squid:S3577") // This is NOT to be run as part of an automated test suite.
class ItemGroupProcessedSendInTiltProducer {

    private static final String TOPIC = "item-group-processed-send";

    private static final Logger LOGGER = LoggerFactory.getLogger(
        "ItemGroupProcessedSendInTiltProducer");

    private static final int MESSAGE_WAIT_TIMEOUT_SECONDS = 10;

    @Configuration
    static class Config {

        @Bean
        KafkaProducer<String, ItemGroupProcessedSend> testProducer(
            @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
            final Map<String, Object> config = new HashMap<>();
            config.put(ProducerConfig.ACKS_CONFIG, "all");
            config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, false);
            return new KafkaProducer<>(
                config,
                new StringSerializer(),
                (topic, data) -> {
                    try {
                        return new SerializerFactory().getSpecificRecordSerializer(
                            ItemGroupProcessedSend.class).toBinary(data); //creates a leading space
                    } catch (SerializationException e) {
                        throw new RuntimeException(e);
                    }
                });
        }

    }

    @Autowired
    private KafkaProducer<String, ItemGroupProcessedSend> testProducer;

    @SuppressWarnings("squid:S2699") // at least one assertion
    @Test
    void produceMessageToTilt() throws InterruptedException, ExecutionException, TimeoutException {
        final Future<RecordMetadata> future = testProducer.send(new ProducerRecord<>(
            TOPIC, 0, System.currentTimeMillis(), SAME_PARTITION_KEY, ITEM_GROUP_PROCESSED_SEND));
        final RecordMetadata result = future.get(MESSAGE_WAIT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        final int partition = result.partition();
        final long offset = result.offset();
        LOGGER.info("Message " + ITEM_GROUP_PROCESSED_SEND + " delivered to topic " + TOPIC
            + " on partition " + partition + " with offset " + offset + ".");
    }
}
