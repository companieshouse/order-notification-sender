package uk.gov.companieshouse.ordernotification.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.email.EmailSend;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.Map;
import java.util.concurrent.ExecutionException;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.ORDER_REFERENCE_NUMBER;
import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.createLogMapWithAcknowledgedKafkaMessage;
import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.logIfNotNull;

@Service
public class EmailSendMessageProducer {

    private static final Logger LOGGER = LoggingUtils.getLogger();

    private final EmailSendMessageFactory emailSendAvroSerializer;
    private final EmailSendKafkaProducer emailSendKafkaProducer;

    public EmailSendMessageProducer(final EmailSendMessageFactory avroSerializer,
                                    final EmailSendKafkaProducer kafkaMessageProducer) {
        this.emailSendAvroSerializer = avroSerializer;
        this.emailSendKafkaProducer = kafkaMessageProducer;
    }

    /**
     * Sends an email-send message to the Kafka producer.
     * @param email EmailSend object encapsulating the message content
     * @throws SerializationException should there be a failure to serialize the EmailSend object
     * @throws ExecutionException should the production of the message to the topic error for some reason
     * @throws InterruptedException should the execution thread be interrupted
     */
    public void sendMessage(final EmailSend email, String orderReference)
            throws SerializationException, ExecutionException, InterruptedException {
        Map<String, Object> logMap =
                LoggingUtils.logWithOrderReference("Sending message to kafka producer", orderReference);
        final Message message = emailSendAvroSerializer.createMessage(email, orderReference);
        logIfNotNull(logMap, LoggingUtils.TOPIC, message.getTopic());
        emailSendKafkaProducer.sendMessage(message, orderReference,
                recordMetadata ->
                    logOffsetFollowingSendIngOfMessage(orderReference, recordMetadata));
    }

    /**
     * Logs the order reference, topic, partition and offset for the item message produced to a Kafka topic.
     * @param orderReference the order reference
     * @param recordMetadata the metadata for a record that has been acknowledged by the server for the message produced
     */
    void logOffsetFollowingSendIngOfMessage(final String orderReference,
                                            final RecordMetadata recordMetadata) {
        final Map<String, Object> logMapCallback =  createLogMapWithAcknowledgedKafkaMessage(recordMetadata);
        logIfNotNull(logMapCallback, ORDER_REFERENCE_NUMBER, orderReference);
        LOGGER.info("Message sent to Kafka topic", logMapCallback);
    }
}
