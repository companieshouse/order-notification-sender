package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.Item;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEventFactory;

@Service
public class ItemGroupProcessedSendHandler implements ApplicationEventPublisherAware {

    private final SendOrderNotificationEventFactory eventFactory;
    private final Logger logger;
    private ApplicationEventPublisher applicationEventPublisher;

    public ItemGroupProcessedSendHandler(SendOrderNotificationEventFactory eventFactory,
        Logger logger) {
        this.eventFactory = eventFactory;
        this.logger = logger;
    }

    public void handleMessage(Message<ItemGroupProcessedSend> message) {
        final ItemGroupProcessedSend payload = message.getPayload();
        logger.info("processing item-group-processed-send message: " + payload, getLogMap(payload));

        // TODO DCAC-295: Send the relevant information onwards via the email-send topic.
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
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