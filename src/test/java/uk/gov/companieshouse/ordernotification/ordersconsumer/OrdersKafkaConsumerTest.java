package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;

import org.springframework.messaging.MessageHeaders;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;

import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.emailsender.KafkaMessagingException;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;
import uk.gov.companieshouse.orders.OrderReceived;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrdersKafkaConsumerTest {
    private static final String ORDER_RECEIVED_URI = "/order/ORDER-12345";
    private static final String ORDER_RECEIVED_TOPIC = "order-received";
    private static final String ORDER_RECEIVED_KEY = "order-received";
    private static final String ORDER_RECEIVED_TOPIC_RETRY = "order-received-retry";
    private static final String ORDER_RECEIVED_TOPIC_ERROR = "order-received-error";
    private static final String PROCESSING_ERROR_MESSAGE = "Order processing failed.";

    @InjectMocks
    private OrdersKafkaConsumer ordersKafkaConsumer;
    @Mock
    private SerializerFactory serializerFactory;
    @Mock
    private AvroSerializer serializer;
    @Captor
    ArgumentCaptor<String> orderUriArgument;
    @Captor
    ArgumentCaptor<String> currentTopicArgument;
    @Captor
    ArgumentCaptor<String> nextTopicArgument;
    @Mock
    private ApplicationEventPublisher applicationEventPublisher;
    @Mock
    LoggingUtils loggingUtils;
    @Mock
    Logger logger;

    @BeforeEach
    void setup() {
        ordersKafkaConsumer.setApplicationEventPublisher(applicationEventPublisher);
    }

    @Test
    void testHandlesOrderReceivedMessage() {
        // Given
        org.springframework.messaging.Message<OrderReceived> actualMessage = createTestMessage(ORDER_RECEIVED_TOPIC);
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(loggingUtils.getMessageHeadersAsMap(any())).thenReturn(Collections.singletonMap("key", "value"));

        // When
        ordersKafkaConsumer.processOrderReceived(actualMessage);

        // Then
        verify(applicationEventPublisher).publishEvent(new SendOrderNotificationEvent(ORDER_RECEIVED_URI, 0));
        verify(loggingUtils, times(2)).logIfNotNull(Collections.singletonMap("key", "value"), LoggingUtils.ORDER_URI, ORDER_RECEIVED_URI);
        verify(logger).info("'order-received' message received", Collections.singletonMap("key", "value"));
        verify(logger).info("Order received message processing completed", Collections.singletonMap("key", "value"));
    }

    @Test
    @Disabled
    void createRetryMessageBuildsMessageSuccessfully() {
        // Given & When
        OrdersKafkaConsumer consumerUnderTest =
                new OrdersKafkaConsumer(new SerializerFactory(),
                        new KafkaListenerEndpointRegistry(),
                        loggingUtils);
        Message actualMessage = consumerUnderTest.createRetryMessage(ORDER_RECEIVED_URI, ORDER_RECEIVED_TOPIC);
        byte[] actualMessageRawValue = actualMessage.getValue();
        // Then
        OrderReceivedDeserializer deserializer = new OrderReceivedDeserializer();
        String actualOrderReceived = (String) deserializer.deserialize(ORDER_RECEIVED_TOPIC, actualMessageRawValue).get(0);
        assertEquals(actualOrderReceived, Matchers.is(ORDER_RECEIVED_URI));
    }

    @Test
    @Disabled
    void republishMessageToRetryTopicRunsSuccessfully()
            throws ExecutionException, InterruptedException, SerializationException {
        // Given & When
        when(serializerFactory.getGenericRecordSerializer(OrderReceived.class)).thenReturn(serializer);
        when(serializer.toBinary(any())).thenReturn(new byte[4]);
        ordersKafkaConsumer.republishMessageToTopic(ORDER_RECEIVED_URI, ORDER_RECEIVED_TOPIC, ORDER_RECEIVED_TOPIC_RETRY);
        // Then
        //verify(ordersKafkaProducer, times(1)).sendMessage(any());
    }

    @Test
    @Disabled
    void republishMessageToRetryTopicThrowsSerializationException()
            throws ExecutionException, InterruptedException, SerializationException {
        // Given & When
        when(serializerFactory.getGenericRecordSerializer(OrderReceived.class)).thenReturn(serializer);
        when(serializer.toBinary(any())).thenThrow(SerializationException.class);
        ordersKafkaConsumer.republishMessageToTopic(ORDER_RECEIVED_URI, ORDER_RECEIVED_TOPIC, ORDER_RECEIVED_TOPIC_RETRY);
        // Then
        //verify(ordersKafkaProducer, times(1)).sendMessage(any());
    }

    @Test
    @Disabled
    void republishMessageToErrorTopicRunsSuccessfully()
            throws ExecutionException, InterruptedException, SerializationException {
        // Given & When
        when(serializerFactory.getGenericRecordSerializer(OrderReceived.class)).thenReturn(serializer);
        when(serializer.toBinary(any())).thenReturn(new byte[4]);
        ordersKafkaConsumer.republishMessageToTopic(ORDER_RECEIVED_URI, ORDER_RECEIVED_TOPIC_RETRY, ORDER_RECEIVED_TOPIC_ERROR);
        // Then
        //verify(ordersKafkaProducer, times(1)).sendMessage(any());
    }

    @Test
    @Disabled
    void republishMessageSuccessfullyCalledForFirstMainMessageOnRetryableErrorException()
            throws SerializationException {
        // Given & When
        when(serializerFactory.getGenericRecordSerializer(OrderReceived.class)).thenReturn(serializer);
        when(serializer.toBinary(any())).thenReturn(new byte[4]);
        doThrow(new RetryableErrorException(PROCESSING_ERROR_MESSAGE)).when(ordersKafkaConsumer).logMessageReceived(any(), any());
        ordersKafkaConsumer.handleMessage(createTestMessage(ORDER_RECEIVED_TOPIC));
        // Then
        verify(ordersKafkaConsumer, times(1)).republishMessageToTopic(orderUriArgument.capture(),
                currentTopicArgument.capture(), nextTopicArgument.capture());
        assertEquals(ORDER_RECEIVED_URI, orderUriArgument.getValue());
        assertEquals(ORDER_RECEIVED_TOPIC, currentTopicArgument.getValue());
        assertEquals(ORDER_RECEIVED_TOPIC_RETRY, nextTopicArgument.getValue());
    }

    @Test
    @Disabled
    void republishMessageNotCalledForFirstRetryMessageOnRetryableErrorException() {
        // Given & When
        doThrow(new RetryableErrorException(PROCESSING_ERROR_MESSAGE)).doNothing().when(ordersKafkaConsumer).logMessageReceived(any(), any());
        ordersKafkaConsumer.handleMessage(createTestMessage(ORDER_RECEIVED_TOPIC_RETRY));
        // Then
        verify(ordersKafkaConsumer, times(0)).republishMessageToTopic(anyString(), anyString(), anyString());
    }

    @Test
    @Disabled
    void republishMessageNotCalledForFirstErrorMessageOnRetryableErrorException() {
        // Given & When
        doThrow(new RetryableErrorException(PROCESSING_ERROR_MESSAGE)).doNothing().when(ordersKafkaConsumer).logMessageReceived(any(), any());
        ordersKafkaConsumer.handleMessage(createTestMessage(ORDER_RECEIVED_TOPIC_ERROR));
        // Then
        verify(ordersKafkaConsumer, times(0)).republishMessageToTopic(anyString(), anyString(), anyString());
    }

    @Test
    @Disabled
    void republishMessageNotCalledOnNonRetryableErrorException() {
        // Given & When

        doThrow(new OrderProcessingException()).when(ordersKafkaConsumer).logMessageReceived(any(), any());
        ordersKafkaConsumer.handleMessage(createTestMessage(ORDER_RECEIVED_TOPIC));
        // Then
        verify(ordersKafkaConsumer, times(0)).republishMessageToTopic(anyString(), anyString(), anyString());
    }

    @Test
    @Disabled
    void republishMessageNotCalledOnNonRetryableKafkaMessagingException() {
        // Given & When
        doThrow(new KafkaMessagingException(PROCESSING_ERROR_MESSAGE, new Exception())).when(ordersKafkaConsumer).logMessageReceived(any(), any());
        ordersKafkaConsumer.handleMessage(createTestMessage(ORDER_RECEIVED_TOPIC));
        // Then
        verify(ordersKafkaConsumer, times(0)).republishMessageToTopic(anyString(), anyString(), anyString());
    }

    @Test
    @Disabled
    void mainListenerExceptionIsCorrectlyHandled() {
        // Given & When
        doThrow(new RetryableErrorException(PROCESSING_ERROR_MESSAGE)).when(ordersKafkaConsumer).processOrderReceived(any());
        RetryableErrorException exception = Assertions.assertThrows(RetryableErrorException.class, () -> {
            ordersKafkaConsumer.processOrderReceived(any());
        });
        // Then
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, PROCESSING_ERROR_MESSAGE);
        verify(ordersKafkaConsumer, times(1)).processOrderReceived(any());
    }

    @Test
    @Disabled
    void retryListenerExceptionIsCorrectlyHandled() {
        // Given & When
        doThrow(new RetryableErrorException(PROCESSING_ERROR_MESSAGE)).when(ordersKafkaConsumer).processOrderReceivedRetry(any());
        RetryableErrorException exception = Assertions.assertThrows(RetryableErrorException.class, () -> {
            ordersKafkaConsumer.processOrderReceivedRetry(any());
        });
        // Then
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, PROCESSING_ERROR_MESSAGE);
        verify(ordersKafkaConsumer, times(1)).processOrderReceivedRetry(any());
    }

    @Test
    @Disabled
    void errorListenerExceptionIsCorrectlyHandled() {
        // Given & When
        doThrow(new RetryableErrorException(PROCESSING_ERROR_MESSAGE)).when(ordersKafkaConsumer).processOrderReceivedError(any());
        RetryableErrorException exception = Assertions.assertThrows(RetryableErrorException.class, () -> {
            ordersKafkaConsumer.processOrderReceivedError(any());
        });
        // Then
        String actualMessage = exception.getMessage();
        assertEquals(actualMessage, PROCESSING_ERROR_MESSAGE);
        verify(ordersKafkaConsumer, times(1)).processOrderReceivedError(any());
    }

    private static org.springframework.messaging.Message<OrderReceived> createTestMessage(String receivedTopic) {
        return new org.springframework.messaging.Message<OrderReceived>() {
            @Override
            public OrderReceived getPayload() {
                OrderReceived orderReceived = new OrderReceived();
                orderReceived.setOrderUri(ORDER_RECEIVED_URI);
                return orderReceived;
            }

            @Override
            public MessageHeaders getHeaders() {
                Map<String, Object> headerItems = new HashMap<>();
                headerItems.put("kafka_receivedTopic", receivedTopic);
                headerItems.put("kafka_offset", 0);
                headerItems.put("kafka_receivedMessageKey", ORDER_RECEIVED_KEY);
                headerItems.put("kafka_receivedPartitionId", 0);
                MessageHeaders headers = new MessageHeaders(headerItems);
                return headers;
            }
        };
    }
}
