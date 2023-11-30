package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.getLogMap;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.logging.Logger;

@Service
public class ItemGroupProcessedSendHandler {

    private final Logger logger;

    public ItemGroupProcessedSendHandler(Logger logger) {
        this.logger = logger;
    }

    public void handleMessage(Message<ItemGroupProcessedSend> message) {
        final ItemGroupProcessedSend payload = message.getPayload();
        logger.info("processing item-group-processed-send message: " + payload, getLogMap(payload));

        // TODO DCAC-295: Send the relevant information onwards via the email-send topic.
    }

}