package uk.gov.companieshouse.ordernotification.errorhandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.ordernotification.eventmodel.EventSourceRetrievable;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.messageproducer.MessageProducer;
import uk.gov.companieshouse.orders.OrderReceived;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * Handles errors raised when processing order notifications.
 */
@Service
public class ErrorHandlingService {

    private static final String RETRY_TOPIC = "order-received-notification-retry";
    private static final String ERROR_TOPIC = "order-received-notification-error";

    private final MessageProducer messageProducer;
    private final LoggingUtils loggingUtils;
    private final int maxRetries;

    @Autowired
    public ErrorHandlingService(MessageProducer messageProducer, LoggingUtils loggingUtils, @Value("${maximum.retries}") int maxRetries) {
        this.messageProducer = messageProducer;
        this.loggingUtils = loggingUtils;
        this.maxRetries = maxRetries;
    }

    /**
     * Handles errors raised when processing order notifications. If the number of configured retries has been exceeded,
     * a message will be published to the error topic, otherwise a message will be sent to the retry topic.
     *
     * @param event The event that an error was raised for.
     * @throws ErrorHandlerFailureException If an error occurs when handling the error.
     */
    @EventListener
    public void handleEvent(EventSourceRetrievable event) {
        Map<String, Object> logArgs = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(logArgs, LoggingUtils.ORDER_URI, event.getEventSource().getOrderURI());
        try {
            if(event.getEventSource().getRetryCount() < maxRetries) {
                loggingUtils.getLogger().debug("Publishing message to retry topic", logArgs);
                messageProducer.sendMessage(
                        new OrderReceived(event.getEventSource().getOrderURI(), event.getEventSource().getRetryCount() + 1),
                        event.getEventSource().getOrderURI(), RETRY_TOPIC);
            } else {
                loggingUtils.getLogger().debug("Maximum number of attempts exceeded; publishing message to error topic", logArgs);
                messageProducer.sendMessage(
                        new OrderReceived(event.getEventSource().getOrderURI(), 0),
                        event.getEventSource().getOrderURI(), ERROR_TOPIC);
            }
        } catch (SerializationException | ExecutionException | TimeoutException e) {
            loggingUtils.getLogger().error("Failed to handle error", e, logArgs);
            throw new ErrorHandlerFailureException("Failed to handle error", e);
        } catch(InterruptedException e) {
            loggingUtils.getLogger().error("Interrupted", e, logArgs);
            Thread.currentThread().interrupt();
            throw new ErrorHandlerFailureException("Interrupted", e);
        }
    }
}
