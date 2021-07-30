package uk.gov.companieshouse.ordernotification.errorhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.ordernotification.eventmodel.EventSourceRetrievable;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.messageproducer.MessageProducer;
import uk.gov.companieshouse.orders.OrderReceived;
import uk.gov.companieshouse.orders.OrderReceivedNotificationRetry;

import java.util.Map;

@Service
public class ErrorHandlingService {

    private static final String RETRY_TOPIC = "order-received-notification-retry";
    private static final String ERROR_TOPIC = "order-received-notification-error";

    private MessageProducer messageProducer;
    private LoggingUtils loggingUtils;
    private int maxRetries;

    @Autowired
    public ErrorHandlingService(MessageProducer messageProducer, LoggingUtils loggingUtils, @Value("${maximum.retries}") int maxRetries) {
        this.messageProducer = messageProducer;
        this.loggingUtils = loggingUtils;
        this.maxRetries = maxRetries;
    }

    @EventListener
    public void handleEvent(EventSourceRetrievable event) {
        try {
            Map<String, Object> logArgs = loggingUtils.createLogMap();
            loggingUtils.logIfNotNull(logArgs, LoggingUtils.ORDER_REFERENCE_NUMBER, event.getEventSource().getOrderReference());
            if(event.getEventSource().getRetryCount() < maxRetries) {
                loggingUtils.getLogger().debug("Publishing message to retry topic", logArgs);
                messageProducer.sendMessage(new OrderReceivedNotificationRetry(new OrderReceived(event.getEventSource().getOrderReference()), event.getEventSource().getRetryCount() + 1), event.getEventSource().getOrderReference(), RETRY_TOPIC);
            } else {
                loggingUtils.getLogger().debug("Maximum number of attempts exceeded; publishing message to error topic", logArgs);
                messageProducer.sendMessage(new OrderReceived(event.getEventSource().getOrderReference()), event.getEventSource().getOrderReference(), ERROR_TOPIC);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
