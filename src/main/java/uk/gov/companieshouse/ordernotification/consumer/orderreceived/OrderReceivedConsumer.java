package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.orders.OrderReceived;

/**
 * <p>Consumes order-received messages and notifies the application that an order is
 * ready to be processed.</p>
 */
@Service
public class OrderReceivedConsumer {

    private final OrderReceivedHandler orderReceivedHandler;

    public OrderReceivedConsumer(OrderReceivedHandler orderReceivedHandler) {
        this.orderReceivedHandler = orderReceivedHandler;
    }

    /**
     * <p>Consumes a message from the order-received topic and notifies the application that an order
     * is ready to be published.</p>
     *
     * @param message A {@link Message message} containing an
     *                {@link OrderReceived order received entity}.
     */
    @KafkaListener(id = "#{'${kafka.topics.order-received-group}'}",
            groupId = "#{'${kafka.topics.order-received-group}'}",
            topics = "#{'${kafka.topics.order-received}'}",
            autoStartup = "#{!${uk.gov.companieshouse.order-notification-sender.error-consumer}}",
            containerFactory = "kafkaOrderReceivedListenerContainerFactory")
    public void processOrderReceived(Message<OrderReceived> message) {
        orderReceivedHandler.handleMessage(message);
    }

    /**
     * <p>Consumes a message from the order-received-retry topic and notifies the application that an order
     * is ready to be published.</p>
     *
     * @param message A {@link Message message} containing an
     *                {@link OrderReceived order received entity}.
     */
    @KafkaListener(id = "#{'${kafka.topics.order-received-retry-group}'}",
            groupId = "#{'${kafka.topics.order-received-retry-group}'}",
            topics = "#{'${kafka.topics.order-received-retry}'}",
            autoStartup = "#{!${uk.gov.companieshouse.order-notification-sender.error-consumer}}",
            containerFactory = "kafkaOrderReceivedListenerContainerFactory")
    public void processOrderReceivedRetry(Message<OrderReceived> message) {
        orderReceivedHandler.handleMessage(message);
    }
}
