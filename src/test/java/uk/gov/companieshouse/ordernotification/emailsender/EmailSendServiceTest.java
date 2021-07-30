package uk.gov.companieshouse.ordernotification.emailsender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.messageproducer.MessageProducer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailSendServiceTest {

    @InjectMocks
    private EmailSendService emailSendService;

    @Mock
    private MessageProducer producer;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private SendEmailEvent event;

    @Mock
    private EmailSend emailSendModel;

    @Mock
    private Logger logger;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Test
    void testHandleEventNoExceptionsThrown() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        when(event.getEmailModel()).thenReturn(emailSendModel);
        when(event.getOrderReference()).thenReturn(TestConstants.ORDER_REFERENCE_NUMBER);

        //when
        emailSendService.handleEvent(event);

        //then
        verify(producer).sendMessage(emailSendModel, TestConstants.ORDER_REFERENCE_NUMBER, "email-send");
    }

    @Test
    void testHandleEventSerializationExceptionThrown() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        doThrow(SerializationException.class).when(producer).sendMessage(any(), any(), any());

        //when
        Executable actual = () -> emailSendService.handleEvent(event);

        //then
        NonRetryableFailureException exception = assertThrows(NonRetryableFailureException.class, actual);
        assertEquals("Failed to serialize email data as avro", exception.getMessage());
    }

    @Test
    void testHandleEventExecutionException() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        Map<String, Object> logMap = new HashMap<>();

        when(loggingUtils.getLogger()).thenReturn(logger);
        when(loggingUtils.createLogMap()).thenReturn(logMap);
        doThrow(ExecutionException.class).when(producer).sendMessage(any(), any(), any());
        emailSendService.setApplicationEventPublisher(applicationEventPublisher);

        //when
        Executable actual = () -> emailSendService.handleEvent(event);

        //then
        assertDoesNotThrow(actual);
        verify(logger).error(eq("Error sending email data to Kafka"), any(), eq(logMap));
        verify(applicationEventPublisher).publishEvent(new EmailSendFailedEvent(event));
    }

    @Test
    void testHandleInterruptedExceptionThrown() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        doThrow(InterruptedException.class).when(producer).sendMessage(any(), any(), any());

        //when
        Executable actual = () -> emailSendService.handleEvent(event);

        //then
        NonRetryableFailureException exception = assertThrows(NonRetryableFailureException.class, actual);
        assertEquals("Interrupted", exception.getMessage());
    }

    @Test
    void testHandleTimeoutExceptionThrown() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        Map<String, Object> logMap = new HashMap<>();

        when(loggingUtils.getLogger()).thenReturn(logger);
        when(loggingUtils.createLogMap()).thenReturn(logMap);
        doThrow(TimeoutException.class).when(producer).sendMessage(any(), any(), any());
        emailSendService.setApplicationEventPublisher(applicationEventPublisher);

        //when
        Executable actual = () -> emailSendService.handleEvent(event);

        //then
        assertDoesNotThrow(actual);
        verify(logger).error(eq("Error sending email data to Kafka"), any(), eq(logMap));
        verify(applicationEventPublisher).publishEvent(new EmailSendFailedEvent(event));
    }
}
