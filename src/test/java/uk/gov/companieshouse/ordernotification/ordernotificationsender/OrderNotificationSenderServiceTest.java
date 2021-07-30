package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderNotificationEnrichable;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsender.SendEmailEvent;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderNotificationSenderServiceTest {

    @InjectMocks
    private OrderNotificationSenderService orderNotificationSenderService;

    @Mock
    private OrderNotificationEnrichable orderNotificationEnricher;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private Logger logger;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private SendOrderNotificationEvent sendOrderNotificationEvent;

    @Mock
    private EmailSend emailSend;

    @Test
    void testPublishEmailSendEventWhenSendOrderNotificationEventHandled() throws OrdersResponseException {
        //given
        when(orderNotificationEnricher.enrich(any())).thenReturn(emailSend);
        when(sendOrderNotificationEvent.getOrderURL()).thenReturn(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        when(sendOrderNotificationEvent.getRetryCount()).thenReturn(1);
        when(loggingUtils.getLogger()).thenReturn(logger);
        Map<String, Object> data = new HashMap<>();
        when(loggingUtils.createLogMap()).thenReturn(data);
        orderNotificationSenderService.setEventPublisher(eventPublisher);

        //when
        orderNotificationSenderService.handleEvent(sendOrderNotificationEvent);

        //then
        verify(eventPublisher).publishEvent(new SendEmailEvent(TestConstants.ORDER_NOTIFICATION_REFERENCE, 1, emailSend));
        verify(orderNotificationEnricher).enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verify(logger).debug("Successfully enriched order; notifying email sender", data);
        verify(loggingUtils).logIfNotNull(data, LoggingUtils.ORDER_REFERENCE_NUMBER, TestConstants.ORDER_NOTIFICATION_REFERENCE);
    }

    @Test
    void testPublishOrderEnrichmentFailedEventWhenOrdersResponseExceptionThrown() throws OrdersResponseException {
        //given
        when(orderNotificationEnricher.enrich(any())).thenThrow(OrdersResponseException.class);
        when(sendOrderNotificationEvent.getOrderURL()).thenReturn(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        when(loggingUtils.getLogger()).thenReturn(logger);
        Map<String, Object> data = new HashMap<>();
        when(loggingUtils.createLogMap()).thenReturn(data);
        orderNotificationSenderService.setEventPublisher(eventPublisher);

        //when
        orderNotificationSenderService.handleEvent(sendOrderNotificationEvent);

        //then
        verify(eventPublisher).publishEvent(new OrderEnrichmentFailedEvent(sendOrderNotificationEvent));
        verify(orderNotificationEnricher).enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verify(logger).error(eq("Failed to enrich order; notifying error handler"), any(), same(data));
        verify(loggingUtils).logIfNotNull(data, LoggingUtils.ORDER_REFERENCE_NUMBER, TestConstants.ORDER_NOTIFICATION_REFERENCE);
    }
}
