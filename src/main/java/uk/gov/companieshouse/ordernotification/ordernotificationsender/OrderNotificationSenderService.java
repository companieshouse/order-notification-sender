package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderNotificationEnrichable;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsender.SendEmailEvent;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

import java.util.Map;

/**
 * Handles an order notification by enriching it with data fetched from the orders API.
 */
@Service
public class OrderNotificationSenderService {

    private final OrderNotificationEnrichable orderEnricher;
    private final LoggingUtils loggingUtils;
    private ApplicationEventPublisher eventPublisher;

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
        loggingUtils.logIfNotNull(loggerArgs, LoggingUtils.ORDER_URI, sendOrderNotificationEvent.getOrderURL());
        try {
            EmailSend emailSend = orderEnricher.enrich(sendOrderNotificationEvent.getOrderURL());
            loggingUtils.getLogger().debug("Successfully enriched order; notifying email sender", loggerArgs);
            eventPublisher.publishEvent(new SendEmailEvent(sendOrderNotificationEvent.getOrderURL(), sendOrderNotificationEvent.getRetryCount(), emailSend));
        } catch (OrdersResponseException e) {
            loggingUtils.getLogger().error("Failed to enrich order; notifying error handler", e, loggerArgs);
            eventPublisher.publishEvent(new OrderEnrichmentFailedEvent(sendOrderNotificationEvent));
        }
    }

    public void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}
