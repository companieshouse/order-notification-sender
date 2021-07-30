package uk.gov.companieshouse.ordernotification.emailsender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmailSendServiceTest {

    @InjectMocks
    private EmailSendService emailSendService;

    @Mock
    private EmailSendMessageProducer producer;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private SendEmailEvent event;

    @Mock
    private EmailSend emailSendModel;

    @Test
    void testHandleEventNoExceptionsThrown() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        when(event.getEmailModel()).thenReturn(emailSendModel);
        when(event.getOrderReference()).thenReturn(TestConstants.ORDER_REFERENCE_NUMBER);

        //when
        emailSendService.handleEvent(event);

        //then
        verify(producer).sendMessage(emailSendModel, TestConstants.ORDER_REFERENCE_NUMBER);
    }

    @Test
    void testHandleEventSerializationExceptionThrown() throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        //given
        doThrow(SerializationException.class).when(producer).sendMessage(any(), any());

        //when
        Executable actual = () -> emailSendService.handleEvent(event);

        //then
        NonRetryableFailureException exception = assertThrows(NonRetryableFailureException.class, actual);
        assertEquals("Failed to serialize email data as avro", exception.getMessage());
    }

    

}
