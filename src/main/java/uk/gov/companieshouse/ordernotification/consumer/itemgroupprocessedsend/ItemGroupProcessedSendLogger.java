package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.ITEM_ID;

import java.util.Map;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

@Aspect
@Component
class ItemGroupProcessedSendLogger {

    private final LoggingUtils loggingUtils;

    public ItemGroupProcessedSendLogger(final LoggingUtils loggingUtils) {
        this.loggingUtils = loggingUtils;
    }

    @Pointcut("execution(public void "
        + "uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend.ItemGroupProcessedSendHandler.handleMessage(..)) "
        + "&& args(message)")
    void handleMessage(Message<ItemGroupProcessedSend> message) {
        // Pointcut
    }

    @Before(value = "handleMessage(message)", argNames = "message")
    public void logMessageReceived(Message<ItemGroupProcessedSend> message) {
        final Map<String, Object> logMap = loggingUtils.getMessageHeadersAsMap(message);
        loggingUtils.logIfNotNull(logMap, ITEM_ID, message.getPayload().getItem().getId());
        loggingUtils.getLogger()
                .info("'" + message.getHeaders().get("kafka_receivedTopic") + "' message received", logMap);
    }

    @After(value = "handleMessage(message)", argNames = "message")
    public void logMessageProcessed(Message<ItemGroupProcessedSend> message) {
        final Map<String, Object> logMap = loggingUtils.getMessageHeadersAsMap(message);
        loggingUtils.logIfNotNull(logMap, ITEM_ID, message.getPayload().getItem().getId());
        loggingUtils.getLogger().info("'" + message.getHeaders().get("kafka_receivedTopic") +
            "' message processing completed", logMap);
    }
}
