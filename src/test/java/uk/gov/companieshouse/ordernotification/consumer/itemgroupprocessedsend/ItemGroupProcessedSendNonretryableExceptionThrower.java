package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import org.springframework.messaging.Message;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.emailsender.NonRetryableFailureException;

public class ItemGroupProcessedSendNonretryableExceptionThrower implements ItemGroupProcessedSendHandler {

    @Override
    public void handleMessage(Message<ItemGroupProcessedSend> message) {
        throw new NonRetryableFailureException("Unable to handle message");
    }
}
