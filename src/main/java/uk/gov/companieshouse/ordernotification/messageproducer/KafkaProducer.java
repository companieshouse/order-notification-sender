package uk.gov.companieshouse.ordernotification.messageproducer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

@Service
class KafkaProducer {
    private final LoggingUtils loggingUtils;
    private final CHKafkaProducer chKafkaProducer;
    private final Long timeout;

    @Autowired
    public KafkaProducer(LoggingUtils loggingUtils, CHKafkaProducer chKafkaProducer, @Value("${kafkaProducer.producerTimeout}") Long timeout) {
        this.loggingUtils = loggingUtils;
        this.chKafkaProducer = chKafkaProducer;
        this.timeout = timeout;
    }

    /**
     * Sends message to Kafka topic
     * @param message certificate or certified copy order message to be produced to the <code>email-send</code> topic
     * @param orderReference the reference of the order
     * @param asyncResponseLogger RecordMetadata {@link Consumer} that can be implemented to allow the logging of
     *                            the offset once the message has been produced
     * @throws ExecutionException should the production of the message to the topic error for some reason
     * @throws InterruptedException should the execution thread be interrupted
     * @throws TimeoutException when the timeout limit is reached
     */
    public void sendMessage(final Message message,
                            final String orderReference,
                            final Consumer<RecordMetadata> asyncResponseLogger)
            throws ExecutionException, InterruptedException, TimeoutException {
        loggingUtils.logMessageWithOrderUri(message, "Sending message to Kafka", orderReference);

        final Future<RecordMetadata> recordMetadataFuture = chKafkaProducer.sendAndReturnFuture(message);
        asyncResponseLogger.accept(recordMetadataFuture.get(timeout, TimeUnit.SECONDS));
    }

}
