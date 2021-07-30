package uk.gov.companieshouse.ordernotification.errorhandler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.eventmodel.EventSourceRetrievable;
import uk.gov.companieshouse.ordernotification.eventmodel.OrderIdentifiable;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.messageproducer.MessageProducer;
import uk.gov.companieshouse.orders.OrderReceived;
import uk.gov.companieshouse.orders.OrderReceivedNotificationRetry;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ErrorHandlingServiceTest {

    private ErrorHandlingService errorHandlingService;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private Logger logger;

    @Mock
    private EventSourceRetrievable eventSourceRetrievable;

    @Mock
    private OrderIdentifiable orderIdentifiable;

    @Mock
    private MessageProducer messageProducer;

    @BeforeEach
    void setup() {
        errorHandlingService = new ErrorHandlingService(messageProducer, loggingUtils, 3);
    }

    @Test
    void testRepublishOrderNotificationToRetryTopicIfRetryCountNotExceeded() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        Map<String, Object> logArgs = new HashMap<>();
        when(orderIdentifiable.getOrderURL()).thenReturn(TestConstants.ORDER_REFERENCE_NUMBER);
        when(orderIdentifiable.getRetryCount()).thenReturn(0);
        when(eventSourceRetrievable.getEventSource()).thenReturn(orderIdentifiable);
        when(loggingUtils.createLogMap()).thenReturn(logArgs);
        when(loggingUtils.getLogger()).thenReturn(logger);

        //when
        errorHandlingService.handleEvent(eventSourceRetrievable);

        //then
        verify(messageProducer).sendMessage(new OrderReceivedNotificationRetry(new OrderReceived(TestConstants.ORDER_REFERENCE_NUMBER), 1), TestConstants.ORDER_REFERENCE_NUMBER, "order-received-notification-retry");
        verify(logger).debug("Publishing message to retry topic", logArgs);
    }

    @Test
    void testPublishOrderNotificationToErrorTopicIfRetryCountExceeded() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        Map<String, Object> logArgs = new HashMap<>();
        when(orderIdentifiable.getOrderURL()).thenReturn(TestConstants.ORDER_REFERENCE_NUMBER);
        when(orderIdentifiable.getRetryCount()).thenReturn(3);
        when(eventSourceRetrievable.getEventSource()).thenReturn(orderIdentifiable);
        when(loggingUtils.createLogMap()).thenReturn(logArgs);
        when(loggingUtils.getLogger()).thenReturn(logger);

        //when
        errorHandlingService.handleEvent(eventSourceRetrievable);

        //then
        verify(messageProducer).sendMessage(new OrderReceived(TestConstants.ORDER_REFERENCE_NUMBER), TestConstants.ORDER_REFERENCE_NUMBER, "order-received-notification-error");
        verify(logger).debug("Maximum number of attempts exceeded; publishing message to error topic", logArgs);
    }

    @Test
    void testThrowRuntimeExceptionIfSerializationException() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        Map<String, Object> logArgs = new HashMap<>();
        when(orderIdentifiable.getOrderURL()).thenReturn(TestConstants.ORDER_REFERENCE_NUMBER);
        when(orderIdentifiable.getRetryCount()).thenReturn(3);
        when(eventSourceRetrievable.getEventSource()).thenReturn(orderIdentifiable);
        when(loggingUtils.createLogMap()).thenReturn(logArgs);
        when(loggingUtils.getLogger()).thenReturn(logger);
        doThrow(SerializationException.class).when(messageProducer).sendMessage(any(), any(), any());

        //when
        Executable actual = () -> errorHandlingService.handleEvent(eventSourceRetrievable);

        //then
        assertThrows(ErrorHandlerFailureException.class, actual);
        verify(logger).error("Failed to handle error", logArgs);
    }

    @Test
    void testThrowRuntimeExceptionIfExecutionException() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        Map<String, Object> logArgs = new HashMap<>();
        when(orderIdentifiable.getOrderURL()).thenReturn(TestConstants.ORDER_REFERENCE_NUMBER);
        when(orderIdentifiable.getRetryCount()).thenReturn(3);
        when(eventSourceRetrievable.getEventSource()).thenReturn(orderIdentifiable);
        when(loggingUtils.createLogMap()).thenReturn(logArgs);
        when(loggingUtils.getLogger()).thenReturn(logger);
        doThrow(ExecutionException.class).when(messageProducer).sendMessage(any(), any(), any());

        //when
        Executable actual = () -> errorHandlingService.handleEvent(eventSourceRetrievable);

        //then
        assertThrows(ErrorHandlerFailureException.class, actual);
        verify(logger).error("Failed to handle error", logArgs);
    }

    @Test
    void testThrowRuntimeExceptionIfInterruptedException() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        Map<String, Object> logArgs = new HashMap<>();
        when(orderIdentifiable.getOrderURL()).thenReturn(TestConstants.ORDER_REFERENCE_NUMBER);
        when(orderIdentifiable.getRetryCount()).thenReturn(3);
        when(eventSourceRetrievable.getEventSource()).thenReturn(orderIdentifiable);
        when(loggingUtils.createLogMap()).thenReturn(logArgs);
        when(loggingUtils.getLogger()).thenReturn(logger);
        doThrow(InterruptedException.class).when(messageProducer).sendMessage(any(), any(), any());

        //when
        Executable actual = () -> errorHandlingService.handleEvent(eventSourceRetrievable);

        //then
        assertThrows(ErrorHandlerFailureException.class, actual);
        verify(logger).error("Failed to handle error", logArgs);
    }

    @Test
    void testThrowRuntimeExceptionIfTimeoutException() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        Map<String, Object> logArgs = new HashMap<>();
        when(orderIdentifiable.getOrderURL()).thenReturn(TestConstants.ORDER_REFERENCE_NUMBER);
        when(orderIdentifiable.getRetryCount()).thenReturn(3);
        when(eventSourceRetrievable.getEventSource()).thenReturn(orderIdentifiable);
        when(loggingUtils.createLogMap()).thenReturn(logArgs);
        when(loggingUtils.getLogger()).thenReturn(logger);
        doThrow(TimeoutException.class).when(messageProducer).sendMessage(any(), any(), any());

        //when
        Executable actual = () -> errorHandlingService.handleEvent(eventSourceRetrievable);

        //then
        assertThrows(ErrorHandlerFailureException.class, actual);
        verify(logger).error("Failed to handle error", logArgs);
    }
}
