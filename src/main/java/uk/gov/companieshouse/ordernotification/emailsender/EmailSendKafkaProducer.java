package uk.gov.companieshouse.ordernotification.emailsender;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

@Service
class EmailSendKafkaProducer {
    private LoggingUtils loggingUtils;
    private CHKafkaProducer chKafkaProducer;

    @Autowired
    public EmailSendKafkaProducer(LoggingUtils loggingUtils, CHKafkaProducer chKafkaProducer) {
        this.loggingUtils = loggingUtils;
        this.chKafkaProducer = chKafkaProducer;
    }

    /**
     * Sends message to Kafka topic
     * @param message certificate or certified copy order message to be produced to the <code>email-send</code> topic
     * @param orderReference the reference of the order
     * @param asyncResponseLogger RecordMetadata {@link Consumer} that can be implemented to allow the logging of
     *                            the offset once the message has been produced
     * @throws ExecutionException should the production of the message to the topic error for some reason
     * @throws InterruptedException should the execution thread be interrupted
     */
    public void sendMessage(final Message message,
                            final String orderReference,
                            final Consumer<RecordMetadata> asyncResponseLogger)
            throws ExecutionException, InterruptedException {
        loggingUtils.logMessageWithOrderReference(message, "Sending message to Kafka", orderReference);

        final Future<RecordMetadata> recordMetadataFuture = chKafkaProducer.sendAndReturnFuture(message);
        asyncResponseLogger.accept(recordMetadataFuture.get());
    }

}
