package uk.gov.companieshouse.ordernotification.ordersconsumer;

import java.util.Map;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;
import uk.gov.companieshouse.orders.OrderReceived;

@Service
public class OrderMessageHandler implements ApplicationEventPublisherAware {

    private final Logger logger;
    private final LoggingUtils loggingUtils;
    private ApplicationEventPublisher applicationEventPublisher;

    public OrderMessageHandler(final Logger logger, final LoggingUtils loggingUtils) {
        this.logger = logger;
        this.loggingUtils = loggingUtils;
    }

    /**
     * Handles processing of received message.
     *
     * @param message received
     */
    public void handleMessage(Message<OrderReceived> message) {
        String orderReceivedUri = message.getPayload().getOrderUri();
        logMessageReceived(message, orderReceivedUri);

        applicationEventPublisher.publishEvent(new SendOrderNotificationEvent(orderReceivedUri,
                message.getPayload().getAttempt()));

        logMessageProcessed(message, orderReceivedUri);
    }

    private void logMessageReceived(Message<?> message, String orderUri) {
        Map<String, Object> logMap = loggingUtils.getMessageHeadersAsMap(message);
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, orderUri);
        loggingUtils.getLogger().info("'" + message.getHeaders().get("kafka_receivedTopic") + "' message received", logMap);
    }

    private void logMessageProcessed(Message<?> message, String orderUri) {
        Map<String, Object> logMap = loggingUtils.getMessageHeadersAsMap(message);
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, orderUri);
        loggingUtils.getLogger().info("Order received message processing completed", logMap);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}