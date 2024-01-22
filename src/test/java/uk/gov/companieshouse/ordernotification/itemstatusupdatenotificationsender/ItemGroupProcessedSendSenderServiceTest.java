package uk.gov.companieshouse.ordernotification.itemstatusupdatenotificationsender;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.companieshouse.itemgroupprocessedsend.Item;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.consumer.orderreceived.RetryableErrorException;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderResourceItemReadyNotificationEnricher;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsender.SendItemReadyEmailEvent;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.messageproducer.MessageProducer;

@ExtendWith(MockitoExtension.class)
class ItemGroupProcessedSendSenderServiceTest {

    @InjectMocks
    private ItemGroupProcessedSendSenderService itemGroupProcessedSendSenderService;

    @Mock
    private MessageProducer producer;

    @Mock
    private ItemGroupProcessedSend event;

    @Mock
    private EmailSend emailSendModel;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private OrderResourceItemReadyNotificationEnricher orderEnricher;

    @Mock
    private Logger logger;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private Item item;

    @Test
    void testHandleEventNoExceptionsThrown() {

        // given
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(event.getItem()).thenReturn(item);

        // when
        itemGroupProcessedSendSenderService.handleEvent(event);

        // then
        verify(logger).debug(
            eq("Successfully enriched item group item status update; notifying email sender"),
            any());

        verify(applicationEventPublisher).publishEvent(any(SendItemReadyEmailEvent.class));
    }

    @Test
    void testRetryableErrorExceptionIsLoggedAndPropagated() {

        // given
        when(orderEnricher.enrich(anyString(), eq(event))).thenThrow(
            new RetryableErrorException("Test exception", new Throwable("Test throwable")));
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(event.getItem()).thenReturn(item);

        // when and then
        final RetryableErrorException exception = assertThrows(RetryableErrorException.class,
            () -> itemGroupProcessedSendSenderService.handleEvent(event));

        verify(logger).error(eq("Failed to enrich item group item status update"), eq(exception),
            anyMap());
        assertThat(exception.getMessage(), is("Test exception"));
        assertThat(exception.getCause().getMessage(), is("Test throwable"));
    }

}