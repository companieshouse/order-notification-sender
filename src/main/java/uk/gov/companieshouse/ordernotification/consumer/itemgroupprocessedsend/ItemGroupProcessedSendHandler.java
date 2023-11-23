package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import java.util.Map;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.Item;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.util.DataMap;

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

    private Map<String, Object> getLogMap(final ItemGroupProcessedSend message) {
        final Item item = message.getItem();
        return new DataMap.Builder()
            .orderId(message.getOrderNumber())
            .groupItem(message.getGroupItem())
            .itemId(item.getId())
            .status(item.getStatus())
            .digitalDocumentLocation(item.getDigitalDocumentLocation())
            .build()
            .getLogMap();
    }
}