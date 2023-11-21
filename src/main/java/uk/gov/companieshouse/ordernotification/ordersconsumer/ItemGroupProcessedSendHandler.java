package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.logging.Logger;
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
        logger.info("processing item-group-processed-send message: " + message.getPayload());

        // TODO: Write a handler for this (another ticket?)
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}