package uk.gov.companieshouse.ordernotification.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.StructuredLogger;
import uk.gov.companieshouse.ordernotification.email.EmailSend;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.Map;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ORDER_REFERENCE;
import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.ORDER_REFERENCE_NUMBER;
import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.TOPIC;


/**
 * Unit tests the {@link EmailSendMessageProducer} class.
 */
@ExtendWith(MockitoExtension.class)
public class EmailSendMessageProducerTest {

    private static final String TOPIC_NAME = "topic";

    @InjectMocks
    private EmailSendMessageProducer messageProducerUnderTest;

    @Mock
    private EmailSendMessageFactory emailSendMessageFactory;

    @Mock
    private EmailSendKafkaProducer emailSendKafkaProducer;

    @Mock
    private Message message;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private StructuredLogger structuredLogger;

    @Mock
    private RecordMetadata recordMetadata;

    @Mock
    private EmailSend emailSend;

    @Test
    @DisplayName("sendMessage delegates message creation to EmailSendMessageFactory")
    void sendMessageDelegatesMessageCreation() throws Exception {

        // Given
        when(emailSendMessageFactory.createMessage(emailSend, ORDER_REFERENCE)).thenReturn(message);

        // When
        messageProducerUnderTest.sendMessage(emailSend, ORDER_REFERENCE);

        // Then
        verify(emailSendMessageFactory).createMessage(emailSend, ORDER_REFERENCE);
    }

    @Test
    @DisplayName("sendMessage delegates message sending to EmailSendKafkaProducer")
    void sendMessageDelegatesMessageSending() throws Exception {

        // Given
        when(emailSendMessageFactory.createMessage(emailSend, ORDER_REFERENCE)).thenReturn(message);

        // When
        messageProducerUnderTest.sendMessage(emailSend, ORDER_REFERENCE);

        // Then
        verify(emailSendKafkaProducer).sendMessage(eq(message), eq(ORDER_REFERENCE), any(Consumer.class));
    }

    @Test
    public void sendMessageMeetsLoggingRequirements() throws Exception {
        // Given
        when(emailSendMessageFactory.createMessage(emailSend, ORDER_REFERENCE)).thenReturn(message);
        when(message.getTopic()).thenReturn(TOPIC_NAME);

        // When
        messageProducerUnderTest.sendMessage(emailSend, ORDER_REFERENCE);

        // Then
        verify(loggingUtils).logWithOrderReference(eq("Sending message to kafka producer"), eq(ORDER_REFERENCE));
        verify(loggingUtils).logIfNotNull(any(), eq(TOPIC), eq(TOPIC_NAME));
    }


    @Test
    public void logOffsetFollowingSendIngOfMessageMeetsLoggingRequirements() {

        // Given
        when(loggingUtils.getLogger()).thenReturn(structuredLogger);

        // When
        messageProducerUnderTest.logOffsetFollowingSendIngOfMessage(ORDER_REFERENCE, recordMetadata);

        // Then
        verify(loggingUtils, times(1)).createLogMapWithAcknowledgedKafkaMessage(recordMetadata);
        verify(loggingUtils).logIfNotNull(any(Map.class), eq(ORDER_REFERENCE_NUMBER), eq((Object)ORDER_REFERENCE));
        verify(loggingUtils.getLogger()).info(eq("Message sent to Kafka topic"), any(Map.class));
   }
}
