package uk.gov.companieshouse.ordernotification.messageproducer;

import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.ORDER_URI;

@Service
public class MessageProducer {

    private final MessageFactory avroSerialiser;
    private final KafkaProducer kafkaProducer;
    private final LoggingUtils loggingUtils;

    public MessageProducer(final MessageFactory avroSerialiser,
                           final KafkaProducer kafkaMessageProducer,
                           final LoggingUtils loggingUtils) {
        this.avroSerialiser = avroSerialiser;
        this.kafkaProducer = kafkaMessageProducer;
        this.loggingUtils = loggingUtils;
    }

    /**
     * Sends a message to the Kafka producer.
     * @param record The record containing content that will be serialised
     * @throws SerializationException should there be a failure to serialise an object
     * @throws ExecutionException should the production of the message to the topic error for some reason
     * @throws InterruptedException should the execution thread be interrupted
     * @throws TimeoutException when the kafka producer timeout elapses
     */
    public void sendMessage(final GenericRecord record, String orderUri, String topic)
            throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        loggingUtils.logWithOrderUri("Sending message to kafka producer", orderUri);
        final Message message = avroSerialiser.createMessage(record, orderUri, topic);
        kafkaProducer.sendMessage(message, orderUri,
                recordMetadata ->
                    logOffsetFollowingSendIngOfMessage(orderUri, recordMetadata));
    }

    /**
     * Logs the order reference, topic, partition and offset for the item message produced to a Kafka topic.
     * @param orderUri the order uri
     * @param recordMetadata the metadata for a record that has been acknowledged by the server for the message produced
     */
    void logOffsetFollowingSendIngOfMessage(final String orderUri,
                                            final RecordMetadata recordMetadata) {
        final Map<String, Object> logMapCallback =  loggingUtils.createLogMapWithAcknowledgedKafkaMessage(recordMetadata);
        loggingUtils.logIfNotNull(logMapCallback, ORDER_URI, orderUri);
        loggingUtils.getLogger().debug("Message sent to Kafka topic", logMapCallback);
    }
}
