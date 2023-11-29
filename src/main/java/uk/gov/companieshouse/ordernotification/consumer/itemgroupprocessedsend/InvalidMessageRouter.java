package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

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
            // TODO DCAC-279 Use structured logging
            LOGGER.info("Producing invalid message " + producerRecord.value() + " to topic "
                + invalidMessageTopic + ".");
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
        this.messageFlags = (MessageFlags) configs.get("message.flags");
        this.invalidMessageTopic = (String) configs.get("invalid.message.topic");
    }
}
