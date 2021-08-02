package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;

import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;
import uk.gov.companieshouse.orders.OrderReceived;
import uk.gov.companieshouse.orders.OrderReceivedNotificationRetry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersKafkaConsumerTest {

    @InjectMocks
    private OrdersKafkaConsumer ordersKafkaConsumer;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    LoggingUtils loggingUtils;
    @Mock
    Logger logger;
    @Mock
    KafkaListenerEndpointRegistry registry;
    @Mock
    MessageListenerContainer listenerContainer;

    @BeforeEach
    void setup() {
        ordersKafkaConsumer.setApplicationEventPublisher(applicationEventPublisher);
    }

    @Test
    void testHandlesOrderReceivedMessage() {
        // Given
        org.springframework.messaging.Message<OrderReceived> actualMessage = createTestMessage(TestConstants.ORDER_RECEIVED_TOPIC, 0);
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(loggingUtils.getMessageHeadersAsMap(any())).thenReturn(Collections.singletonMap("key", "value"));

        // When
        ordersKafkaConsumer.processOrderReceived(actualMessage);

        // Then
        verify(applicationEventPublisher).publishEvent(new SendOrderNotificationEvent(TestConstants.ORDER_NOTIFICATION_REFERENCE, 0));
        verify(loggingUtils, times(2)).logIfNotNull(Collections.singletonMap("key", "value"), LoggingUtils.ORDER_URI, TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verify(logger).info("'order-received' message received", Collections.singletonMap("key", "value"));
        verify(logger).info("Order received message processing completed", Collections.singletonMap("key", "value"));
    }

    @Test
    void testHandlesOrderReceivedNotificationRetryMessage() {
        // Given
        org.springframework.messaging.Message<OrderReceivedNotificationRetry> actualMessage = createRetryMessage();
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(loggingUtils.getMessageHeadersAsMap(any())).thenReturn(Collections.singletonMap("key", "value"));

        // When
        ordersKafkaConsumer.processOrderReceivedRetry(actualMessage);

        // Then
        verify(applicationEventPublisher).publishEvent(new SendOrderNotificationEvent(TestConstants.ORDER_NOTIFICATION_REFERENCE, 2));
        verify(loggingUtils, times(2)).logIfNotNull(Collections.singletonMap("key", "value"), LoggingUtils.ORDER_URI, TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verify(logger).info("'order-received-notification-retry' message received", Collections.singletonMap("key", "value"));
        verify(logger).info("Order received message processing completed", Collections.singletonMap("key", "value"));
    }

    @Test
    void testHandlesOrderReceivedErrorMessage() {
        // Given
        org.springframework.messaging.Message<OrderReceived> actualMessage = createTestMessage(TestConstants.ORDER_RECEIVED_TOPIC_ERROR, 0);
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(loggingUtils.getMessageHeadersAsMap(any())).thenReturn(Collections.singletonMap("key", "value"));

        // When
        ordersKafkaConsumer.processOrderReceivedError(actualMessage);

        // Then
        verify(applicationEventPublisher).publishEvent(new SendOrderNotificationEvent(TestConstants.ORDER_NOTIFICATION_REFERENCE, 0));
        verify(loggingUtils, times(2)).logIfNotNull(Collections.singletonMap("key", "value"), LoggingUtils.ORDER_URI, TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verify(logger).info("'order-received-notification-error' message received", Collections.singletonMap("key", "value"));
        verify(logger).info("Order received message processing completed", Collections.singletonMap("key", "value"));
    }

    @Test
    void stopProcessingErrorOffsetsIfRecoveryOffsetExceeded() {
        // Given
        org.springframework.messaging.Message<OrderReceived> actualMessage = createTestMessage(TestConstants.ORDER_RECEIVED_TOPIC_ERROR, 1);
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(registry.getListenerContainer(anyString())).thenReturn(listenerContainer);

        // When
        ordersKafkaConsumer.processOrderReceivedError(actualMessage);

        // Then
        verifyNoInteractions(applicationEventPublisher);
        verify(logger).info(eq("Pausing error consumer as error recovery offset reached."), any());
        verify(listenerContainer).pause();
    }

    private static org.springframework.messaging.Message<OrderReceived> createTestMessage(String receivedTopic, int offset) {
        return new org.springframework.messaging.Message<OrderReceived>() {
            @Override
            public OrderReceived getPayload() {
                return getOrderReceived();
            }

            @Override
            public MessageHeaders getHeaders() {
               return new MessageHeaders(OrdersKafkaConsumerTest.getHeaders(receivedTopic, offset));
            }
        };
    }

    private static OrderReceived getOrderReceived() {
        OrderReceived orderReceived = new OrderReceived();
        orderReceived.setOrderUri(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        return orderReceived;
    }

    private static Map<String, Object> getHeaders(String receivedTopic, int offset) {
        Map<String, Object> headerItems = new HashMap<>();
        headerItems.put("kafka_receivedTopic", receivedTopic);
        headerItems.put("kafka_offset", offset);
        headerItems.put("kafka_receivedMessageKey", TestConstants.ORDER_RECEIVED_KEY);
        headerItems.put("kafka_receivedPartitionId", 0);
        return new MessageHeaders(headerItems);
    }

    private static org.springframework.messaging.Message<OrderReceivedNotificationRetry> createRetryMessage() {
        OrderReceivedNotificationRetry orderReceivedNotificationRetry = new OrderReceivedNotificationRetry();
        orderReceivedNotificationRetry.setOrder(getOrderReceived());
        orderReceivedNotificationRetry.setAttempt(2);
        return new GenericMessage<>(orderReceivedNotificationRetry, getHeaders(TestConstants.ORDER_RECEIVED_TOPIC_RETRY, 0));
    }
}
