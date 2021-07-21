package uk.gov.companieshouse.ordernotification.emailsender.service;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.ITEM_ID;
import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.ORDER_REFERENCE_NUMBER;

@Service
public class ItemKafkaProducer extends KafkaProducer {

    @Autowired
    public ItemKafkaProducer(LoggingUtils loggingUtils) {
        super(loggingUtils);
    }

    /**
     * Sends (produces) message to the Kafka <code>chd-item-ordered</code> topic.
     * @param orderReference the reference of the order to which the item belongs
     * @param itemId the ID of the item that the message to be sent represents
     * @param message missing image delivery item message to be produced to the <code>chd-item-ordered</code> topic
     * @param asyncResponseLogger RecordMetadata {@link Consumer} that can be implemented to allow the logging of
     *                            the offset once the message has been produced
     * @throws ExecutionException should the production of the message to the topic error for some reason
     * @throws InterruptedException should the execution thread be interrupted
     */
    @Async
    public void sendMessage(final String orderReference,
                            final String itemId,
                            final Message message,
                            final Consumer<RecordMetadata> asyncResponseLogger)
            throws ExecutionException, InterruptedException {

        final Map<String, Object> logMap = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(logMap, ORDER_REFERENCE_NUMBER, orderReference);
        loggingUtils.logIfNotNull(logMap, ITEM_ID, itemId);
        loggingUtils.getLogger().info("Sending message to kafka topic", logMap);

        final Future<RecordMetadata> recordMetadataFuture = getChKafkaProducer().sendAndReturnFuture(message);
        asyncResponseLogger.accept(recordMetadataFuture.get());
    }

    @Override
    protected void modifyProducerConfig(final ProducerConfig producerConfig) {
        producerConfig.setMaxBlockMilliseconds(10000);
    }
}
