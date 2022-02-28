package uk.gov.companieshouse.ordernotification.ordersconsumer;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

@Component
public class ErrorConsumerController {
    private final Logger logger;
    private final LoggingUtils loggingUtils;
    private final String errorGroup;
    private final String errorTopic;
    private final PartitionOffset partitionOffset;
    private final KafkaListenerEndpointRegistry registry;

    public ErrorConsumerController(Logger logger, LoggingUtils loggingUtils,
                                   @Value("${kafka.topics.order-received-notification-error-group}") String errorGroup,
                                   @Value("${kafka.topics.order-received-notification-error}") String errorTopic,
                                   PartitionOffset partitionOffset,
                                   KafkaListenerEndpointRegistry registry) {
        this.logger = logger;
        this.loggingUtils = loggingUtils;
        this.errorGroup = errorGroup;
        this.errorTopic = errorTopic;
        this.partitionOffset = partitionOffset;
        this.registry = registry;
    }

    public void pauseConsumerThread() {
        Map<String, Object> logMap = loggingUtils.createLogMap();
        logMap.put(errorGroup, partitionOffset.getOffset());
        logMap.put(LoggingUtils.TOPIC, errorTopic);
        logger.info("Pausing error consumer as error recovery offset reached.", logMap);
        Optional.ofNullable(registry.getListenerContainer(errorGroup)).ifPresent(
                MessageListenerContainer::pause);
    }

    void resumeConsumerThread() {
        Map<String, Object> logMap = loggingUtils.createLogMap();
        logMap.put(errorGroup, partitionOffset.getOffset());
        logMap.put(LoggingUtils.TOPIC, errorTopic);
        logger.info("Resuming error consumer thread.", logMap);
        Optional.ofNullable(registry.getListenerContainer(errorGroup)).ifPresent(
                MessageListenerContainer::resume);
    }
}
