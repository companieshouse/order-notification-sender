package uk.gov.companieshouse.ordernotification.emailmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.emailsendmodel.MappingException;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrderMapperFactory;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrdersApiMapper;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.OrderRetrievable;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderResourceOrderNotificationEnricherTest {

    @InjectMocks
    private OrderResourceOrderNotificationEnricher enricher;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private OrderMapperFactory factory;

    @Mock
    private OrdersApiMapper mapper;

    @Mock
    private OrderRetrievable orderRetrievable;

    @Mock
    private EmailSend emailSend;

    @Mock
    private OrdersApi ordersApi;

    @Mock
    private BaseItemApi item;

    @Mock
    private Logger logger;

    @Test
    void testEnrichOrderNotificationWithOrderResource() throws OrdersResponseException {
        //given
        when(orderRetrievable.getOrderData(anyString())).thenReturn(ordersApi);
        when(ordersApi.getItems()).thenReturn(Collections.singletonList(item));
        when(item.getKind()).thenReturn("kind");
        when(factory.getOrderMapper(any())).thenReturn(mapper);
        when(mapper.map(any())).thenReturn(emailSend);
        when(loggingUtils.logWithOrderUri(any(), any())).thenReturn(Collections.emptyMap());
        when(loggingUtils.getLogger()).thenReturn(logger);

        //when
        EmailSend actual = enricher.enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE);

        //then
        assertEquals(emailSend, actual);
        verify(orderRetrievable).getOrderData(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verify(factory).getOrderMapper("kind");
        verify(mapper).map(ordersApi);
        verify(loggingUtils).logWithOrderUri("Fetching resource for order", TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verify(logger).debug("Mapping order", Collections.emptyMap());
    }

    @Test
    void testThrowExceptionIfOrdersApiErrors() throws OrdersResponseException {
        //given
        when(orderRetrievable.getOrderData(anyString())).thenThrow(OrdersResponseException.class);
        when(loggingUtils.logWithOrderUri(any(), any())).thenReturn(Collections.emptyMap());

        //when
        Executable actual = () -> enricher.enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE);

        //then
        assertThrows(OrdersResponseException.class, actual);
        verify(orderRetrievable).getOrderData(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verifyNoInteractions(factory);
    }

    @Test
    void testLogRuntimeExceptionThrownByFactory() throws OrdersResponseException {
        //given
        when(orderRetrievable.getOrderData(anyString())).thenReturn(ordersApi);
        when(ordersApi.getItems()).thenReturn(Collections.singletonList(item));
        when(item.getKind()).thenReturn("kind");
        when(factory.getOrderMapper(any())).thenThrow(IllegalArgumentException.class);
        when(loggingUtils.logWithOrderUri(any(), any())).thenReturn(Collections.emptyMap());
        when(loggingUtils.getLogger()).thenReturn(logger);

        //when
        Executable actual = () -> enricher.enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        verify(logger).error("Failed to map order resource", exception, Collections.emptyMap());
    }

    @Test
    void testLogRuntimeExceptionThrownByMapper() throws OrdersResponseException {
        //given
        when(orderRetrievable.getOrderData(anyString())).thenReturn(ordersApi);
        when(ordersApi.getItems()).thenReturn(Collections.singletonList(item));
        when(item.getKind()).thenReturn("kind");
        when(factory.getOrderMapper(any())).thenReturn(mapper);
        when(mapper.map(any())).thenThrow(MappingException.class);
        when(loggingUtils.logWithOrderUri(any(), any())).thenReturn(Collections.emptyMap());
        when(loggingUtils.getLogger()).thenReturn(logger);

        //when
        Executable actual = () -> enricher.enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE);

        //then
        MappingException exception = assertThrows(MappingException.class, actual);
        verify(logger).error("Failed to map order resource", exception, Collections.emptyMap());
    }
}
