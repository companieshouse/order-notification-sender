package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderNotificationEnrichable;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsender.SendEmailEvent;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordersconsumer.RetryableErrorException;

/**
 * Handles an order notification by enriching it with data fetched from the orders API.
 */
@Service
public class OrderNotificationSenderService implements ApplicationEventPublisherAware {

    private final OrderNotificationEnrichable orderEnricher;
    private final LoggingUtils loggingUtils;
    private ApplicationEventPublisher applicationEventPublisher;

    public OrderNotificationSenderService(OrderNotificationEnrichable orderEnricher, LoggingUtils loggingUtils) {
        this.orderEnricher = orderEnricher;
        this.loggingUtils = loggingUtils;
    }

    /**
     * Handles an order notification by enriching it with data fetched from the orders API. If an error occurs when
     * enriching the notification then a failure event will be published.
     *
     * @param sendOrderNotificationEvent The order that is being processed.
     */
    @EventListener
    public void handleEvent(SendOrderNotificationEvent sendOrderNotificationEvent) {
        Map<String, Object> loggerArgs = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(loggerArgs, LoggingUtils.ORDER_URI, sendOrderNotificationEvent.getOrderURI());
        try {
            EmailSend emailSend = orderEnricher.enrich(sendOrderNotificationEvent.getOrderURI());
            loggingUtils.getLogger().debug("Successfully enriched order; notifying email sender", loggerArgs);
            applicationEventPublisher.publishEvent(new SendEmailEvent(sendOrderNotificationEvent.getOrderURI(), sendOrderNotificationEvent.getRetryCount(), emailSend));
        } catch (RetryableErrorException e) {
            loggingUtils.getLogger().error("Failed to enrich order; notifying error handler", e, loggerArgs);
            applicationEventPublisher.publishEvent(new OrderEnrichmentFailedEvent(sendOrderNotificationEvent));
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
