package uk.gov.companieshouse.ordernotification.kafka;

import kafka.log.Log;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.ordernotification.email.EmailSend;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ORDER_REFERENCE;
import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.ORDER_REFERENCE_NUMBER;
import static uk.gov.companieshouse.ordernotification.logging.LoggingUtils.TOPIC;


/**
 * Unit tests the {@link EmailSendMessageProducer} class.
 */
@ExtendWith(MockitoExtension.class)
//@PrepareForTest({LoggingUtils.class, Logger.class})
@SuppressWarnings("squid:S5786") // public class access modifier required for JUnit 4 test
public class EmailSendMessageProducerTest {

    private static final long OFFSET_VALUE = 1L;
    private static final String TOPIC_NAME = "topic";
    private static final int PARTITION_VALUE = 0;

    @InjectMocks
    private EmailSendMessageProducer messageProducerUnderTest;

    @Mock
    private EmailSendMessageFactory emailSendMessageFactory;

    @Mock
    private EmailSendKafkaProducer emailSendKafkaProducer;

    @Mock
    private Message message;

    @Mock
    private Logger logger;

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

    /**
     * This is a JUnit 4 test to take advantage of PowerMock.
     */
    @Test
    public void sendMessageMeetsLoggingRequirements() throws Exception {

        // Given
        ArgumentCaptor<String> arg1 = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

        try (MockedStatic<LoggingUtils> mockLoggingUtils = mockStatic(LoggingUtils.class)) {
            //mockLoggingUtils.verify(LoggingUtils.class, times(1));
            LoggingUtils.logWithOrderReference(arg1.capture(), arg2.capture());

            when(emailSendMessageFactory.createMessage(emailSend, ORDER_REFERENCE)).thenReturn(message);
            when(message.getTopic()).thenReturn(TOPIC_NAME);

            // When
            messageProducerUnderTest.sendMessage(emailSend, ORDER_REFERENCE);
        }

        // Then
        assertEquals("Sending message to kafka producer", arg1.getValue());
        assertEquals(ORDER_REFERENCE, arg2.getValue());
    }

    /**
     * This is a JUnit 4 test to take advantage of PowerMock.
     */
    public void logOffsetFollowingSendIngOfMessageMeetsLoggingRequirements() throws ReflectiveOperationException {

        // Given
        setFinalStaticField(EmailSendMessageProducer.class, "LOGGER", logger);
        mockStatic(LoggingUtils.class);

        when(recordMetadata.topic()).thenReturn(TOPIC_NAME);
        when(recordMetadata.partition()).thenReturn(PARTITION_VALUE);
        when(recordMetadata.offset()).thenReturn(OFFSET_VALUE);

        // When
        messageProducerUnderTest.logOffsetFollowingSendIngOfMessage(ORDER_REFERENCE, recordMetadata);

        // Then
        verifyLoggingAfterMessageAcknowledgedByKafkaServerIsAdequate();

    }

    private void verifyLoggingBeforeMessageSendingIsAdequate() {


        //PowerMockito.verifyStatic(LoggingUtils.class);
        LoggingUtils.logWithOrderReference("Sending message to kafka producer", ORDER_REFERENCE);

        //PowerMockito.verifyStatic(LoggingUtils.class);
        LoggingUtils.logIfNotNull(any(Map.class), eq(TOPIC), eq(TOPIC_NAME));

    }

    private void verifyLoggingAfterMessageAcknowledgedByKafkaServerIsAdequate() {

        //PowerMockito.verifyStatic(LoggingUtils.class);
        LoggingUtils.createLogMapWithAcknowledgedKafkaMessage(recordMetadata);

        //PowerMockito.verifyStatic(LoggingUtils.class);
        LoggingUtils.logIfNotNull(any(Map.class), eq(ORDER_REFERENCE_NUMBER), eq(ORDER_REFERENCE));

        verify(logger).info(eq("Message sent to Kafka topic"), any(Map.class));

    }

    /**
     * Utility method (hack) to allow us to change a private static final field.
     * See https://dzone.com/articles/how-to-change-private-static-final-fields
     * @param clazz the class holding the field
     * @param fieldName the name of the private static final field to set
     * @param value the value to set the field to
     * @throws ReflectiveOperationException should something unexpected happen
     */
    private static void setFinalStaticField(Class<?> clazz, String fieldName, Object value)
            throws ReflectiveOperationException {
        final Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        final Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        field.set(null, value);
    }

}
