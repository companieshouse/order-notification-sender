package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.getLogMap;
import static uk.gov.companieshouse.ordernotification.logging.LoggingUtilsConfiguration.APPLICATION_NAMESPACE;

import java.util.Map;
import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Routes a message to the invalid letter topic if a non-retryable error has been thrown during
 * message processing.
 */
public class InvalidMessageRouter implements ProducerInterceptor<String, ItemGroupProcessedSend> {

    public static final String MESSAGE_FLAGS = "message.flags";
    public static final String INVALID_MESSAGE_TOPIC = "invalid.message.topic";
    public static final String ENABLE_IDEMPOTENCE = "enable.idempotence";

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    private MessageFlags messageFlags;
    private String invalidMessageTopic;

    @Override
    public ProducerRecord<String, ItemGroupProcessedSend> onSend(
        ProducerRecord<String, ItemGroupProcessedSend> producerRecord) {
        if (messageFlags.isRetryable()) {
            messageFlags.destroy();
            return producerRecord;
        } else {
            final ItemGroupProcessedSend message = producerRecord.value();
            LOGGER.error("Producing invalid message " + message + " to topic "
                + invalidMessageTopic + ".", getLogMap(message));
            return new ProducerRecord<>(this.invalidMessageTopic, producerRecord.key(),
                producerRecord.value());
        }
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {
        // Method must be implemented, but no intervention is required here.
    }

    @Override
    public void close() {
        // Method must be implemented, but no intervention is required here.
    }

    @Override
    public void configure(Map<String, ?> configs) {
        this.messageFlags = (MessageFlags) configs.get(MESSAGE_FLAGS);
        this.invalidMessageTopic = (String) configs.get(INVALID_MESSAGE_TOPIC);
    }
}
