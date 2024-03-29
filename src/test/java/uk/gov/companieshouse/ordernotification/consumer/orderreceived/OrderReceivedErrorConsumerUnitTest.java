package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.event.ConsumerStoppedEvent;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.messaging.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.consumer.PartitionOffset;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.orders.OrderReceived;

@ExtendWith(MockitoExtension.class)
class OrderReceivedErrorConsumerUnitTest {

    @Mock
    private Message<OrderReceived> message;

    @Mock
    private KafkaConsumer<String, OrderReceived> consumer;

    @Mock
    private OrderReceivedHandler orderReceivedHandler;

    @Mock
    private ErrorConsumerController errorConsumerController;

    @Spy
    private PartitionOffset partitionOffset = new PartitionOffset();

    @Mock
    private ConsumerStoppedEvent consumerStoppedEvent;

    @Mock
    private KafkaMessageListenerContainer<String, OrderReceived> kafkaMessageListenerContainer;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private Logger logger;

    @InjectMocks
    private OrderReceivedErrorConsumer orderReceivedErrorConsumer;

    @BeforeEach
    void beforeEach() {
        orderReceivedErrorConsumer.setErrorGroup("order-notification-sender-order-received-"
                + "notification-error");
        orderReceivedErrorConsumer.setErrorTopic("order-received-notification-error");
    }

    @Test
    void shouldNotHandleMessageAndShouldStopConsumerWhenOffsetIsEqualToRecoveryOffset() {
        //given
        when(partitionOffset.getOffset()).thenReturn(1L);

        //when
        orderReceivedErrorConsumer.processOrderReceived(message, 1L, consumer);

        //then
        verify(orderReceivedHandler, times(0)).handleMessage(message);
        verify(errorConsumerController).pauseConsumerThread();
    }

    @Test
    void shouldNotHandleMessageAndShouldStopConsumerWhenOffsetIsGreaterThanRecoveryOffset() {
        //given
        when(partitionOffset.getOffset()).thenReturn(1L);

        //when
        orderReceivedErrorConsumer.processOrderReceived(message, 2L, consumer);

        //then
        verify(orderReceivedHandler, times(0)).handleMessage(message);
        verify(errorConsumerController).pauseConsumerThread();
    }

    @Test
    void shouldHandleMessageAndStopConsumerWhenOffsetIsOneLessThanRecoveryOffset() {
        //given
        when(partitionOffset.getOffset()).thenReturn(1L);

        //when
        orderReceivedErrorConsumer.processOrderReceived(message, 0L, consumer);

        //then
        verify(orderReceivedHandler).handleMessage(message);
        verify(errorConsumerController).pauseConsumerThread();
    }

    @Test
    void shouldHandleMessageAndNotStopConsumerWhenOffsetIsTwoLessThanRecoveryOffset() {
        //given
        when(partitionOffset.getOffset()).thenReturn(2L);

        //when
        orderReceivedErrorConsumer.processOrderReceived(message, 0L, consumer);

        //then
        verify(orderReceivedHandler).handleMessage(message);
        verify(errorConsumerController, times(0)).pauseConsumerThread();
    }

    @Test
    void shouldStopConsumerThreadForErrorTopic() {
        //given
        when(consumerStoppedEvent.getSource(KafkaMessageListenerContainer.class)).thenReturn(kafkaMessageListenerContainer);
        when(kafkaMessageListenerContainer.getBeanName()).thenReturn("order-notification-sender"
                + "-order-received-notification-error-0");

        //when
        orderReceivedErrorConsumer.consumerStopped(consumerStoppedEvent);

        //then
        verify(partitionOffset).clear();
    }

    @Test
    void shouldNotStopConsumerThreadForRetryTopic() {
        //given
        when(consumerStoppedEvent.getSource(KafkaMessageListenerContainer.class)).thenReturn(kafkaMessageListenerContainer);
        when(kafkaMessageListenerContainer.getBeanName()).thenReturn("order-notification-sender"
                + "-order-received-notification-retry-0");

        //when
        orderReceivedErrorConsumer.consumerStopped(consumerStoppedEvent);

        //then
        verify(partitionOffset, times(0)).clear();
    }

    @Test
    void shouldNotSetErrorRecoveryOffsetWhenOffsetHasAlreadyBeenSet() {
        //given
        when(partitionOffset.getOffset()).thenReturn(null);
        when(loggingUtils.getLogger()).thenReturn(logger);

        //when
        orderReceivedErrorConsumer.configureErrorRecoveryOffset(consumer);

        //then
        verify(partitionOffset, times(0)).setOffset(1L);
    }

    @Test
    void shouldCorrectlySetErrorRecoveryOffsetFromConsumerEndOffsets() {
        //given
        when(consumer.endOffsets(any())).thenReturn(new HashMap<TopicPartition, Long>() {
            {
                put(new TopicPartition("order-received-notification-error", 0), 1L);
            }
        });

        when(loggingUtils.getLogger()).thenReturn(logger);

        //when
        orderReceivedErrorConsumer.configureErrorRecoveryOffset(consumer);

        //then
        verify(partitionOffset).setOffset(1L);
        verify(logger).info("Setting Error Consumer Recovery Offset to '1'");
    }
}