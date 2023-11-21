package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.orders.OrderReceived;

/**
 * <p>Consumes order-received messages and notifies the application that an order is
 * ready to be processed.</p>
 */
@Service
public class OrdersKafkaConsumer {

    private final OrderMessageHandler orderMessageHandler;

    private final ItemGroupProcessedSendHandler itemGroupProcessedSendHandler;

    public OrdersKafkaConsumer(OrderMessageHandler orderMessageHandler, ItemGroupProcessedSendHandler itemGroupProcessedSendHandler) {
        this.orderMessageHandler = orderMessageHandler;
        this.itemGroupProcessedSendHandler = itemGroupProcessedSendHandler;
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
        orderMessageHandler.handleMessage(message);
    }

    /**
     * <p>Consumes a message from the item-group-processed-send topic</p>
     *
     * @param message A {@link Message message} containing an
     *                {@link ItemGroupProcessedSend entity}.
     */
    @KafkaListener(id = "#{'${kafka.topics.item-group-processed-send-group}'}",
            groupId = "#{'${kafka.topics.item-group-processed-send-group}'}",
            topics = "#{'${kafka.topics.item-group-processed-send}'}",
            autoStartup = "#{!${uk.gov.companieshouse.order-notification-sender.error-consumer}}",
            containerFactory = "kafkaItemGroupProcessedSendListenerContainerFactory")
    public void processItemGroupProcessedSend(Message<ItemGroupProcessedSend> message) {

        //TODO: Add a RetryTopic annotation?
        itemGroupProcessedSendHandler.handleMessage(message);
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
        orderMessageHandler.handleMessage(message);
    }
}
