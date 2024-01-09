package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.getLogMap;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.logging.Logger;

@Service
public class ItemGroupProcessedSendEmailSender implements ItemGroupProcessedSendHandler {

    private final Logger logger;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ItemGroupProcessedSendEmailSender(Logger logger,
        ApplicationEventPublisher applicationEventPublisher) {
        this.logger = logger;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void handleMessage(Message<ItemGroupProcessedSend> message) {
        final ItemGroupProcessedSend payload = message.getPayload();
        logger.info("processing item-group-processed-send message: " + payload, getLogMap(payload));
        // TODO DCAC-295 Do we need to create an event class here?
        applicationEventPublisher.publishEvent(payload);
    }

}