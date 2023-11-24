package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;

/**
 * Consumes <code>item-group-processed-send</code> messages and notifies the application that it is
 * ready to be processed.
 */
@Service
public class ItemGroupProcessedSendConsumer {

    private final ItemGroupProcessedSendHandler itemGroupProcessedSendHandler;

    public ItemGroupProcessedSendConsumer(ItemGroupProcessedSendHandler itemGroupProcessedSendHandler) {
        this.itemGroupProcessedSendHandler = itemGroupProcessedSendHandler;
    }

    /**
     * Consumes a message from the <code>item-group-processed-send</code> topic.
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

        // TODO DCAC-279: Add a retry topic consumer, etc.
        itemGroupProcessedSendHandler.handleMessage(message);
    }

}
