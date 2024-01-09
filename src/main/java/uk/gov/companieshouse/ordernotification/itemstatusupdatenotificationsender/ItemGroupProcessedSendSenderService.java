package uk.gov.companieshouse.ordernotification.itemstatusupdatenotificationsender;

import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.consumer.orderreceived.RetryableErrorException;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderNotificationEnrichable;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsender.SendEmailEvent;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

/**
 * Handles an order notification by enriching it with data fetched from the orders API.
 */
@Service
public class ItemGroupProcessedSendSenderService {

    private final OrderNotificationEnrichable orderEnricher;
    private final LoggingUtils loggingUtils;
    // TODO DCAC-295 Do we need an application event publisher here?
    private final ApplicationEventPublisher applicationEventPublisher;

    public ItemGroupProcessedSendSenderService(OrderNotificationEnrichable orderEnricher, LoggingUtils loggingUtils,
        ApplicationEventPublisher applicationEventPublisher) {
        this.orderEnricher = orderEnricher;
        this.loggingUtils = loggingUtils;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    /**
     * Handles an item group item status update notification by enriching it with data
     * fetched from the orders API. If an error occurs when enriching the notification then a
     * failure event will be published.
     *
     * @param itemGroupProcessedSendEvent The ItemGroupProcessedSend message being processed.
     */
    @EventListener
    public void handleEvent(ItemGroupProcessedSend itemGroupProcessedSendEvent) {
        Map<String, Object> loggerArgs = loggingUtils.createLogMap();
        final String orderUri = "/orders/" + itemGroupProcessedSendEvent.getOrderNumber();
        loggingUtils.logIfNotNull(loggerArgs, LoggingUtils.ORDER_URI, orderUri);
        try {
            final EmailSend emailSend = orderEnricher.enrich(orderUri);
            loggingUtils.getLogger().debug("Successfully enriched item group item status update; notifying email sender", loggerArgs);
            applicationEventPublisher.publishEvent(new SendEmailEvent(orderUri, /* TODO DCAC-295 Refactor? */0, emailSend));

        } catch (RetryableErrorException e) {
            loggingUtils.getLogger().error("Failed to enrich item group item status update", e, loggerArgs);
            // TODO DCAC-295 Hopefully we don't need this - InvalidMessageRouter will ensure retries take place?
            // applicationEventPublisher.publishEvent(new OrderEnrichmentFailedEvent(itemGroupProcessedSendEvent));
            throw e;
        }
    }
}
