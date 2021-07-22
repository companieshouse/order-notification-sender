package uk.gov.companieshouse.ordernotification.ordersproducer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class OrdersKafkaProducer {
    private LoggingUtils loggingUtils;
    private CHKafkaProducer chKafkaProducer;

    @Autowired
    public OrdersKafkaProducer(LoggingUtils loggingUtils, CHKafkaProducer chKafkaProducer) {
        this.loggingUtils = loggingUtils;
        this.chKafkaProducer = chKafkaProducer;
    }

    /**
     * Sends message to Kafka topic
     *
     * @param message message
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void sendMessage(final Message message) throws ExecutionException, InterruptedException {
        Map<String, Object> logMap = loggingUtils.createLogMapWithKafkaMessage(message); //
        loggingUtils.getLogger().info("Sending message to kafka topic", logMap);
        chKafkaProducer.send(message);
    }
}
