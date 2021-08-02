package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;
import uk.gov.companieshouse.orders.OrderReceived;
import uk.gov.companieshouse.orders.OrderReceivedNotificationRetry;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.APPLICATION_NAMESPACE;

/**
 * <p>Consumes order-received messages and notifies the application that an order is
 * ready to be processed.</p>
 */
@Service
public class OrdersKafkaConsumer implements ConsumerSeekAware, ApplicationEventPublisherAware {

    private static final String ORDER_RECEIVED_TOPIC = "order-received";
    private static final String ORDER_RECEIVED_TOPIC_RETRY = "order-received-notification-retry";
    private static final String ORDER_RECEIVED_TOPIC_ERROR = "order-received-notification-error";
    private static final String ORDER_RECEIVED_GROUP =
            APPLICATION_NAMESPACE + "-" + ORDER_RECEIVED_TOPIC;
    private static final String ORDER_RECEIVED_GROUP_RETRY =
            APPLICATION_NAMESPACE + "-" + ORDER_RECEIVED_TOPIC_RETRY;
    private static final String ORDER_RECEIVED_GROUP_ERROR =
            APPLICATION_NAMESPACE + "-" + ORDER_RECEIVED_TOPIC_ERROR;
    private static long errorRecoveryOffset = 0L;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${uk.gov.companieshouse.item-handler.error-consumer}")
    private boolean errorConsumerEnabled;

    private final KafkaListenerEndpointRegistry registry;
    private ApplicationEventPublisher applicationEventPublisher;
    private final LoggingUtils loggingUtils;

    public OrdersKafkaConsumer(KafkaListenerEndpointRegistry registry, LoggingUtils loggingUtils) {
        this.registry = registry;
        this.loggingUtils = loggingUtils;
    }

    /**
     * <p>Consumes a message from the order-received topic and notifies the application that an order
     * is ready to be published.</p>
     * 
     * @param message A {@link org.springframework.messaging.Message message} containing an
     * {@link OrderReceived order received entity}.
     */
    @KafkaListener(id = ORDER_RECEIVED_GROUP, groupId = ORDER_RECEIVED_GROUP,
            topics = ORDER_RECEIVED_TOPIC,
            autoStartup = "#{!${uk.gov.companieshouse.item-handler.error-consumer}}",
            containerFactory = "kafkaOrderReceivedListenerContainerFactory")
    public void processOrderReceived(org.springframework.messaging.Message<OrderReceived> message) {
        handleMessage(new OrderReceivedFacade(message));
    }

    /**
     * <p>Consumes a message from the order-received-retry topic and notifies the application that an order
     * is ready to be published.</p>
     *
     * @param message A {@link org.springframework.messaging.Message message} containing an
     * {@link OrderReceivedNotificationRetry order received retry entity}. This entity contains the original
     * {@link OrderReceived order that was received} and an integer indicating the number of times the message
     * has been resent.
     */
    @KafkaListener(id = ORDER_RECEIVED_GROUP_RETRY, groupId = ORDER_RECEIVED_GROUP_RETRY,
            topics = ORDER_RECEIVED_TOPIC_RETRY,
            autoStartup = "#{!${uk.gov.companieshouse.item-handler.error-consumer}}",
            containerFactory = "kafkaOrderReceivedRetryListenerContainerFactory")
    public void processOrderReceivedRetry(
            org.springframework.messaging.Message<OrderReceivedNotificationRetry> message) {
            handleMessage(new OrderReceivedNotificationRetryFacade(message));
    }

    /**
     * <p>Consumes a message from the order-received-error topic and notifies the application that an order
     * is ready to be published. Messages are only consumed where IS_ERROR_QUEUE_CONSUMER=true and the offset number is
     * less than the most recent offset in the error topic at the time the application was started.</p>
     *
     * @param message A {@link org.springframework.messaging.Message message} containing an
     * {@link OrderReceived order received entity}.
     */
    @KafkaListener(id = ORDER_RECEIVED_GROUP_ERROR, groupId = ORDER_RECEIVED_GROUP_ERROR,
            topics = ORDER_RECEIVED_TOPIC_ERROR,
            autoStartup = "${uk.gov.companieshouse.item-handler.error-consumer}",
            containerFactory = "kafkaListenerContainerFactory")
    public void processOrderReceivedError(
            org.springframework.messaging.Message<OrderReceived> message) {
        long offset = Long.parseLong("" + message.getHeaders().get("kafka_offset"));
        if (offset <= errorRecoveryOffset) {
            handleMessage(new OrderReceivedFacade(message));
        } else {
            Map<String, Object> logMap = loggingUtils.createLogMap();
            logMap.put(LoggingUtils.ORDER_RECEIVED_GROUP_ERROR, errorRecoveryOffset);
            logMap.put(LoggingUtils.TOPIC, ORDER_RECEIVED_TOPIC_ERROR);
            loggingUtils.getLogger().info("Pausing error consumer as error recovery offset reached.",
                    logMap);
            registry.getListenerContainer(ORDER_RECEIVED_GROUP_ERROR).pause();
        }
    }

    private void handleMessage(MessageFacade<?> message) {
        String orderReceivedUri = message.getOrderUri();
        logMessageReceived(message.getMessage(), orderReceivedUri);

        applicationEventPublisher.publishEvent(new SendOrderNotificationEvent(orderReceivedUri,
                message.getRetries()));

        logMessageProcessed(message.getMessage(), orderReceivedUri);
    }

    private void logMessageReceived(org.springframework.messaging.Message<?> message,
            String orderUri) {
        Map<String, Object> logMap = loggingUtils.getMessageHeadersAsMap(message);
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, orderUri);
        loggingUtils.getLogger().info("'" + message.getHeaders().get("kafka_receivedTopic") + "' message received", logMap);
    }

    private void logMessageProcessed(org.springframework.messaging.Message<?> message,
            String orderUri) {
        Map<String, Object> logMap = loggingUtils.getMessageHeadersAsMap(message);
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, orderUri);
        loggingUtils.getLogger().info("Order received message processing completed", logMap);
    }

    private static void updateErrorRecoveryOffset(long offset) {
        errorRecoveryOffset = offset;
    }

    private Map<String, Object> errorConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserialiser.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ORDER_RECEIVED_GROUP_ERROR);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return props;
    }

    /**
     * Sets `errorRecoveryOffset` to latest topic offset (error topic) minus 1, before error
     * consumer starts. This helps the error consumer to stop consuming messages when all messages
     * up to `errorRecoveryOffset` are processed.
     * 
     * @param map map of topics and partitions
     * @param consumerSeekCallback callback that allows a consumers offset position to be moved.
     */
    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> map,
            ConsumerSeekCallback consumerSeekCallback) {
        if (errorConsumerEnabled) {
            try (KafkaConsumer<String, String> consumer =
                    new KafkaConsumer<>(errorConsumerConfigs())) {
                final Map<TopicPartition, Long> topicPartitionsMap =
                        consumer.endOffsets(map.keySet());
                map.forEach((topic, action) -> {
                    updateErrorRecoveryOffset(topicPartitionsMap.get(topic) - 1);
                    loggingUtils.getLogger()
                            .info(String.format("Setting Error Consumer Recovery Offset to '%1$d'",
                                    errorRecoveryOffset));
                });
            }
        }
    }

    @Override
    public void registerSeekCallback(ConsumerSeekCallback consumerSeekCallback) {
        // Do nothing as not required for this implementation
    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> map,
            ConsumerSeekCallback consumerSeekCallback) {
        // Do nothing as not required for this implementation
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
