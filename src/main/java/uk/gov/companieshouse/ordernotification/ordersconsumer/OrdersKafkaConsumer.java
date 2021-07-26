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
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;
import uk.gov.companieshouse.orders.OrderReceived;
import uk.gov.companieshouse.orders.OrderReceivedNotificationRetry;

import java.util.HashMap;
import java.util.Map;

import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.APPLICATION_NAMESPACE;

@Service
public class OrdersKafkaConsumer implements ConsumerSeekAware, ApplicationEventPublisherAware {

    private static final String ORDER_RECEIVED_TOPIC = "order-received";
    private static final String ORDER_RECEIVED_TOPIC_RETRY = "order-received-retry";
    private static final String ORDER_RECEIVED_TOPIC_ERROR = "order-received-error";
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
    private LoggingUtils loggingUtils;

    public OrdersKafkaConsumer(KafkaListenerEndpointRegistry registry, LoggingUtils loggingUtils) {
        this.registry = registry;
        this.loggingUtils = loggingUtils;
    }

    /**
     * Main listener/consumer. Calls `handleMessage` method to process received message.
     * 
     * @param message
     */
    @KafkaListener(id = ORDER_RECEIVED_GROUP, groupId = ORDER_RECEIVED_GROUP,
            topics = ORDER_RECEIVED_TOPIC,
            autoStartup = "#{!${uk.gov.companieshouse.item-handler.error-consumer}}",
            containerFactory = "kafkaListenerContainerFactory")
    public void processOrderReceived(org.springframework.messaging.Message<OrderReceived> message) {
        handleMessage(new OrderReceivedDecorator(message));
    }

    /**
     * Retry (`-retry`) listener/consumer. Calls `handleMessage` method to process received message.
     * 
     * @param message
     */
    @KafkaListener(id = ORDER_RECEIVED_GROUP_RETRY, groupId = ORDER_RECEIVED_GROUP_RETRY,
            topics = ORDER_RECEIVED_TOPIC_RETRY,
            autoStartup = "#{!${uk.gov.companieshouse.item-handler.error-consumer}}",
            containerFactory = "kafkaListenerContainerFactory")
    public void processOrderReceivedRetry(
            org.springframework.messaging.Message<OrderReceivedNotificationRetry> message) {
            handleMessage(new OrderReceivedNotificationRetryDecorator(message));
    }

    /**
     * Error (`-error`) topic listener/consumer is enabled when the application is launched in error
     * mode (IS_ERROR_QUEUE_CONSUMER=true). Receives messages up to `errorRecoveryOffset` offset.
     * Calls `handleMessage` method to process received message. If the `retryable` processor is
     * unsuccessful with a `retryable` error, after maximum numbers of attempts allowed, the message
     * is republished to `-retry` topic for failover processing. This listener stops accepting
     * messages when the topic's offset reaches `errorRecoveryOffset`.
     * 
     * @param message
     */
    @KafkaListener(id = ORDER_RECEIVED_GROUP_ERROR, groupId = ORDER_RECEIVED_GROUP_ERROR,
            topics = ORDER_RECEIVED_TOPIC_ERROR,
            autoStartup = "${uk.gov.companieshouse.item-handler.error-consumer}",
            containerFactory = "kafkaListenerContainerFactory")
    public void processOrderReceivedError(
            org.springframework.messaging.Message<OrderReceived> message) {
        long offset = Long.parseLong("" + message.getHeaders().get("kafka_offset"));
        if (offset <= errorRecoveryOffset) {
            handleMessage(new OrderReceivedDecorator(message));
        } else {
            Map<String, Object> logMap = loggingUtils.createLogMap();
            logMap.put(LoggingUtils.ORDER_RECEIVED_GROUP_ERROR, errorRecoveryOffset);
            logMap.put(LoggingUtils.TOPIC, ORDER_RECEIVED_TOPIC_ERROR);
            loggingUtils.getLogger().info("Pausing error consumer as error recovery offset reached.",
                    logMap);
            registry.getListenerContainer(ORDER_RECEIVED_GROUP_ERROR).pause();
        }
    }

    /**
     * Handles processing of received message.
     * 
     * @param message
     */
    protected void handleMessage(TransformationDecorator<?> message) {
        String orderReceivedUri = message.transform();
        logMessageReceived(message.getMessage(), orderReceivedUri);

        applicationEventPublisher.publishEvent(new SendOrderNotificationEvent(orderReceivedUri,
                message.getRetries()));

        logMessageProcessed(message.getMessage(), orderReceivedUri);
    }

    protected void logMessageReceived(org.springframework.messaging.Message<?> message,
            String orderUri) {
        Map<String, Object> logMap = loggingUtils.getMessageHeadersAsMap(message);
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, orderUri);
        loggingUtils.getLogger().info("'" + message.getHeaders().get("kafka_receivedTopic") + "' message received", logMap);
    }

    private void logMessageProcessed(org.springframework.messaging.Message<?> message,
            String orderUri) {
        Map<String, Object> logMap = loggingUtils.getMessageHeadersAsMap(message);
        loggingUtils.logIfNotNull(logMap, loggingUtils.ORDER_URI, orderUri);
        loggingUtils.getLogger().info("Order received message processing completed", logMap);
    }

    private static void updateErrorRecoveryOffset(long offset) {
        errorRecoveryOffset = offset;
    }

    private Map<String, Object> errorConsumerConfigs() {
        Map<String, Object> props = new HashMap();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, OrderReceivedDeserializer.class);
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
