package uk.gov.companieshouse.ordernotification.ordersconsumer;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtilsConfiguration.APPLICATION_NAMESPACE;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEventFactory;


@Service
public class ItemGroupProcessedSendHandler implements ApplicationEventPublisherAware {

    private final SendOrderNotificationEventFactory eventFactory;

    private ApplicationEventPublisher applicationEventPublisher;

    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAMESPACE);

    public void handleMessage(Message<ItemGroupProcessedSend> message) {
        LOGGER.info("processing item-group-processed-send message: " + message.getPayload());

        // TODO: Write a handler for this (another ticket?)
    }

    public ItemGroupProcessedSendHandler(SendOrderNotificationEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}