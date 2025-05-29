package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.chskafka.PrivateSendEmailHandler;
import uk.gov.companieshouse.api.handler.chskafka.request.PrivateSendEmailPost;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderNotificationEnrichable;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSendFailedEvent;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

@ExtendWith(MockitoExtension.class)
class OrderNotificationSenderServiceTest {

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

    @Mock
    private Supplier<InternalApiClient> apiClient;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateSendEmailHandler privateSendEmailHandler;

    @Mock
    private PrivateSendEmailPost privateSendEmailPost;

    @Test
    void testPublishEmailSendEventWhenSendOrderNotificationEventHandled() throws OrdersResponseException, ApiErrorResponseException {
        //given
        when(orderNotificationEnricher.enrich(any())).thenReturn(emailSend);
        when(sendOrderNotificationEvent.getOrderURI()).thenReturn(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(apiClient.get()).thenReturn(internalApiClient);
        when(internalApiClient.sendEmailHandler()).thenReturn(privateSendEmailHandler);
        when(privateSendEmailHandler.postSendEmail(eq("/send-email"), any())).thenReturn(privateSendEmailPost);
        when(privateSendEmailPost.execute()).thenReturn(new ApiResponse<>(200, null, null));
        Map<String, Object> data = new HashMap<>();
        when(loggingUtils.createLogMap()).thenReturn(data);
        orderNotificationSenderService.setApplicationEventPublisher(eventPublisher);

        //when
        orderNotificationSenderService.handleEvent(sendOrderNotificationEvent);

        //then
        verify(privateSendEmailPost).execute();
        verify(orderNotificationEnricher).enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verify(loggingUtils).logIfNotNull(data, LoggingUtils.ORDER_URI, TestConstants.ORDER_NOTIFICATION_REFERENCE);

        verify(logger, times(1)).debug("Preparing to enrich order; using order enricher...", data);
    }

    @Test
    void testPublishOrderEnrichmentFailedEventWhenOrdersResponseExceptionThrown() throws OrdersResponseException {
        //given
        when(orderNotificationEnricher.enrich(any())).thenThrow(OrdersResponseException.class);
        when(sendOrderNotificationEvent.getOrderURI()).thenReturn(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        when(loggingUtils.getLogger()).thenReturn(logger);
        Map<String, Object> data = new HashMap<>();
        when(loggingUtils.createLogMap()).thenReturn(data);
        orderNotificationSenderService.setApplicationEventPublisher(eventPublisher);

        //when
        orderNotificationSenderService.handleEvent(sendOrderNotificationEvent);

        //then
        verify(eventPublisher).publishEvent(new OrderEnrichmentFailedEvent(sendOrderNotificationEvent));
        verify(orderNotificationEnricher).enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verify(logger).error(eq("Failed to enrich order; notifying error handler"), any(), same(data));
        verify(loggingUtils).logIfNotNull(data, LoggingUtils.ORDER_URI, TestConstants.ORDER_NOTIFICATION_REFERENCE);
    }

    @Test
    void testEmailSendFailedEventWhenApiErrorResponseExceptionThrown() throws OrdersResponseException, ApiErrorResponseException {
        //given
        when(orderNotificationEnricher.enrich(any())).thenReturn(emailSend);
        when(sendOrderNotificationEvent.getOrderURI()).thenReturn(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(apiClient.get()).thenReturn(internalApiClient);
        when(internalApiClient.sendEmailHandler()).thenReturn(privateSendEmailHandler);
        when(privateSendEmailHandler.postSendEmail(eq("/send-email"), any())).thenReturn(privateSendEmailPost);
        when(privateSendEmailPost.execute()).thenThrow(ApiErrorResponseException.class);
        Map<String, Object> data = new HashMap<>();
        when(loggingUtils.createLogMap()).thenReturn(data);
        orderNotificationSenderService.setApplicationEventPublisher(eventPublisher);

        //when
        orderNotificationSenderService.handleEvent(sendOrderNotificationEvent);

        //then
        verify(eventPublisher).publishEvent(new EmailSendFailedEvent(sendOrderNotificationEvent));
        verify(orderNotificationEnricher).enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verify(logger).error(eq("Failed to send email for enriched order; notifying error handler"), any(), same(data));
        verify(loggingUtils).logIfNotNull(data, LoggingUtils.ORDER_URI, TestConstants.ORDER_NOTIFICATION_REFERENCE);
    }
}
