package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import org.springframework.messaging.Message;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;

/**
 * Handles an incoming {@link ItemGroupProcessedSend} message.
 */
public interface ItemGroupProcessedSendHandler {

    /**
     * Handles an incoming {@link ItemGroupProcessedSend} message.
     *
     * @param message {@link Message} wrapping a {@link ItemGroupProcessedSend}
     */
    void handleMessage(Message<ItemGroupProcessedSend> message);
}
