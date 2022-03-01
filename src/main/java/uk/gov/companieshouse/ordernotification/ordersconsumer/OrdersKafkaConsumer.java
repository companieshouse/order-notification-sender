package uk.gov.companieshouse.ordernotification.ordersconsumer;

import static java.util.Objects.isNull;
import static uk.gov.companieshouse.ordernotification.logging.LoggingUtilsConfiguration.APPLICATION_NAMESPACE;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.event.ConsumerStoppedEvent;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.orders.OrderReceived;

/**
 * <p>Consumes order-received messages and notifies the application that an order is
 * ready to be processed.</p>
 */
@Service
public class OrdersKafkaConsumer implements ConsumerSeekAware {

    private static final String ORDER_RECEIVED_TOPIC = "order-received";
    private static final String ORDER_RECEIVED_TOPIC_RETRY = "order-received-notification-retry";
    private static final String ORDER_RECEIVED_TOPIC_ERROR = "order-received-notification-error";
    private static final String ORDER_RECEIVED_GROUP =
            APPLICATION_NAMESPACE + "-" + ORDER_RECEIVED_TOPIC;
    private static final String ORDER_RECEIVED_GROUP_RETRY =
            APPLICATION_NAMESPACE + "-" + ORDER_RECEIVED_TOPIC_RETRY;
    private static final String ORDER_RECEIVED_GROUP_ERROR =
            APPLICATION_NAMESPACE + "-" + ORDER_RECEIVED_TOPIC_ERROR;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${uk.gov.companieshouse.item-handler.error-consumer}")
    private boolean errorConsumerEnabled;

    private final KafkaListenerEndpointRegistry registry;
    private final OrderMessageHandler orderMessageHandler;
    private final PartitionOffset errorRecoveryOffset;
    private final ErrorConsumerController errorConsumerController;
    private final LoggingUtils loggingUtils;

    private static CountDownLatch startupLatch = new CountDownLatch(0);
    private static CountDownLatch eventLatch = new CountDownLatch(0);

    private String errorGroup;
    private String errorTopic;

    public OrdersKafkaConsumer(KafkaListenerEndpointRegistry registry,
            OrderMessageHandler orderMessageHandler,
            PartitionOffset errorRecoveryOffset, ErrorConsumerController errorConsumerController,
            LoggingUtils loggingUtils) {
        this.registry = registry;
        this.orderMessageHandler = orderMessageHandler;
        this.errorRecoveryOffset = errorRecoveryOffset;
        this.errorConsumerController = errorConsumerController;
        this.loggingUtils = loggingUtils;
    }

    /**
     * <p>Consumes a message from the order-received topic and notifies the application that an order
     * is ready to be published.</p>
     * 
     * @param message A {@link Message message} containing an
     * {@link OrderReceived order received entity}.
     */
    @KafkaListener(id = ORDER_RECEIVED_GROUP, groupId = ORDER_RECEIVED_GROUP,
            topics = ORDER_RECEIVED_TOPIC,
            autoStartup = "#{!${uk.gov.companieshouse.item-handler.error-consumer}}",
            containerFactory = "kafkaOrderReceivedListenerContainerFactory")
    public void processOrderReceived(Message<OrderReceived> message) {
        orderMessageHandler.handleMessage(message);
        eventLatch.countDown();
    }

    /**
     * <p>Consumes a message from the order-received-retry topic and notifies the application that an order
     * is ready to be published.</p>
     *
     * @param message A {@link Message message} containing an
     * {@link OrderReceived order received entity}.
     */
    @KafkaListener(id = ORDER_RECEIVED_GROUP_RETRY, groupId = ORDER_RECEIVED_GROUP_RETRY,
            topics = ORDER_RECEIVED_TOPIC_RETRY,
            autoStartup = "#{!${uk.gov.companieshouse.item-handler.error-consumer}}",
            containerFactory = "kafkaOrderReceivedListenerContainerFactory")
    public void processOrderReceivedRetry(Message<OrderReceived> message) {
            orderMessageHandler.handleMessage(message);
            eventLatch.countDown();
    }

    /**
     * Error (`-error`) topic listener/consumer is enabled when the application is launched in error
     * mode (IS_ERROR_QUEUE_CONSUMER=true). Receives messages up to `errorRecoveryOffset` offset.
     * Calls `handleMessage` method to process received message. If the `retryable` processor is
     * unsuccessful with a `retryable` error, after maximum numbers of attempts allowed, the message
     * is republished to `-retry` topic for failover processing. This listener stops accepting
     * messages when the topic's offset reaches `errorRecoveryOffset`.
     *
     * @param message to be processed
     * @param offset Kafka offset of the current message
     * @param consumer Kafka consumer used by current consumer thread
     */
    @KafkaListener(id = ORDER_RECEIVED_GROUP_ERROR, groupId = ORDER_RECEIVED_GROUP_ERROR,
            topics = ORDER_RECEIVED_TOPIC_ERROR,
            autoStartup = "${uk.gov.companieshouse.item-handler.error-consumer}",
            containerFactory = "kafkaOrderReceivedListenerContainerFactory")
    public void processOrderReceivedError(Message<OrderReceived> message,
            @Header(KafkaHeaders.OFFSET) Long offset,
            @Header(KafkaHeaders.CONSUMER) KafkaConsumer<String, OrderReceived> consumer) {
        // Configure recovery offset on first message received after application startup
        configureErrorRecoveryOffset(consumer);

        if (offset < errorRecoveryOffset.getOffset()) {
            orderMessageHandler.handleMessage(message);
            eventLatch.countDown();
        }

        // Stop consumer after offset reached
        if (offset >= errorRecoveryOffset.getOffset() - 1) {
            errorConsumerController.pauseConsumerThread();
        }
    }

    @EventListener
    public void consumerStopped(ConsumerStoppedEvent event) {
        Optional.ofNullable(event.getSource(KafkaMessageListenerContainer.class))
                .flatMap(s -> Optional.ofNullable(s.getBeanName()))
                .ifPresent(name -> {
                    if (name.startsWith(errorGroup)) {
                        errorRecoveryOffset.clear();
                    }
                });
    }

    /**
     * Lazily sets `errorRecoveryOffset` to last topic offset, before first message received is
     * consumed. This helps the error consumer to stop consuming messages when all messages up to
     * `errorRecoveryOffset` are processed.
     */
    void configureErrorRecoveryOffset(KafkaConsumer<String, OrderReceived> consumer) {
        if (!isNull(errorRecoveryOffset.getOffset())) {
            return;
        }
        // Get the end offsets for the consumers partitions i.e. the last un-committed [non-consumed] offsets
        // Note there should [will] only be one entry as this consumer is consuming from a single partition
        Map<TopicPartition, Long> endOffsets = consumer.endOffsets(consumer.assignment());
        Long endOffset = endOffsets.get(new TopicPartition(errorTopic, 0));
        errorRecoveryOffset.setOffset(endOffset);
        loggingUtils.getLogger().info(String.format("Setting Error Consumer Recovery "
                + "Offset to '%1$d'", errorRecoveryOffset.getOffset()));
    }

    static void setStartupLatch(CountDownLatch startupLatch) {
        OrdersKafkaConsumer.startupLatch = startupLatch;
    }

    static void setEventLatch(CountDownLatch eventLatch) {
        OrdersKafkaConsumer.eventLatch = eventLatch;
    }

    void setErrorConsumerEnabled(boolean enabled) {
        this.errorConsumerEnabled = enabled;
    }

    void setErrorGroup(String errorGroup) {
        this.errorGroup = errorGroup;
    }

    void setErrorTopic(String errorTopic) {
        this.errorTopic = errorTopic;
    }
}
