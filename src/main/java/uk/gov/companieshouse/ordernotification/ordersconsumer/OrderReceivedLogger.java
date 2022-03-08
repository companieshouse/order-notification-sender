package uk.gov.companieshouse.ordernotification.ordersconsumer;

import java.util.Map;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.orders.OrderReceived;

@Aspect
@Component
class OrderReceivedLogger {

    private final LoggingUtils loggingUtils;

    public OrderReceivedLogger(final LoggingUtils loggingUtils) {
        this.loggingUtils = loggingUtils;
    }

    @Pointcut("execution(public void uk.gov.companieshouse.ordernotification.ordersconsumer.OrderMessageHandler.handleMessage(..)) && args(message)")
    void handleMessage(Message<OrderReceived> message) {
        // Pointcut
    }

    @Before(value = "handleMessage(message)", argNames = "message")
    public void logMessageReceived(Message<OrderReceived> message) {
        Map<String, Object> logMap = loggingUtils.getMessageHeadersAsMap(message);
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, message.getPayload().getOrderUri());
        loggingUtils.getLogger()
                .info("'" + message.getHeaders().get("kafka_receivedTopic") + "' message received", logMap);
    }

    @After(value = "handleMessage(message)", argNames = "message")
    public void logMessageProcessed(Message<OrderReceived> message) {
        Map<String, Object> logMap = loggingUtils.getMessageHeadersAsMap(message);
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, message.getPayload().getOrderUri());
        loggingUtils.getLogger().info("Order received message processing completed", logMap);
    }
}
