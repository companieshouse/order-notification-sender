package uk.gov.companieshouse.ordernotification.messageproducer;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.StructuredLogger;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageProducerTest {

    @InjectMocks
    private MessageProducer messageProducerUnderTest;

    @Mock
    private MessageFactory messageFactory;

    @Mock
    private KafkaProducer kafkaProducer;

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
    void testSendMessageMarshalsEntityIntoAvroAndProducesKafkaMessage() throws Exception {
        // Given
        when(messageFactory.createMessage(emailSend, TestConstants.ORDER_NOTIFICATION_REFERENCE, TestConstants.KAFKA_TOPIC)).thenReturn(message);

        // When
        messageProducerUnderTest.sendMessage(emailSend, TestConstants.ORDER_NOTIFICATION_REFERENCE, TestConstants.KAFKA_TOPIC);

        // Then
        verify(messageFactory).createMessage(emailSend, TestConstants.ORDER_NOTIFICATION_REFERENCE, TestConstants.KAFKA_TOPIC);
        verify(kafkaProducer).sendMessage(eq(message), eq(TestConstants.ORDER_NOTIFICATION_REFERENCE), any());
        verify(loggingUtils).logWithOrderUri("Sending message to kafka producer", TestConstants.ORDER_NOTIFICATION_REFERENCE);
    }

    @Test
    public void logOffsetFollowingSendIngOfMessageMeetsLoggingRequirements() {
        // Given
        when(loggingUtils.getLogger()).thenReturn(structuredLogger);

        // When
        messageProducerUnderTest.logOffsetFollowingSendIngOfMessage(TestConstants.ORDER_NOTIFICATION_REFERENCE, recordMetadata);

        // Then
        verify(loggingUtils, times(1)).createLogMapWithAcknowledgedKafkaMessage(recordMetadata);
        verify(loggingUtils).logIfNotNull(any(), eq(LoggingUtils.ORDER_URI), eq(TestConstants.ORDER_NOTIFICATION_REFERENCE));
        verify(structuredLogger).debug(eq("Message sent to Kafka topic"), any());
    }
}
