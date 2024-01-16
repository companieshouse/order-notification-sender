package uk.gov.companieshouse.ordernotification.emailmodel;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_GROUP_PROCESSED_SEND;

import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrdersApiDetailsMapper;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.OrderRetrievable;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiWrappable;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersResponseException;

@ExtendWith(MockitoExtension.class)
class OrderResourceItemReadyNotificationEnricherTest {

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private OrderRetrievable orderRetrievable;

    @Mock
    private OrdersApiWrappable ordersApiWrappable;

    @Mock
    private Logger logger;

    @Mock
    private OrdersApiDetailsMapper ordersApiMapper;

    @InjectMocks
    private OrderResourceItemReadyNotificationEnricher enricher;

    @Test
    void testThrowExceptionIfOrdersApiErrorsDuringEnrichmentWithItemGroupProcessedSend()
        throws OrdersResponseException {
        //given
        when(orderRetrievable.getOrderData(anyString())).thenThrow(OrdersResponseException.class);
        when(loggingUtils.logWithOrderUri(any(), any())).thenReturn(Collections.emptyMap());

        //when
        Executable actual = () -> enricher.enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE,
            ITEM_GROUP_PROCESSED_SEND);

        //then
        assertThrows(OrdersResponseException.class, actual);
        verify(orderRetrievable).getOrderData(TestConstants.ORDER_NOTIFICATION_REFERENCE);
        verifyNoInteractions(ordersApiMapper);
    }

    @Test
    void testLogRuntimeExceptionThrownDuringEnrichmentWithItemGroupProcessedSend() {
        //given
        when(orderRetrievable.getOrderData(anyString())).thenReturn(ordersApiWrappable);
        when(ordersApiMapper.mapToEmailSend(any(), any())).thenThrow(
            IllegalArgumentException.class);
        when(loggingUtils.logWithOrderUri(any(), any())).thenReturn(Collections.emptyMap());
        when(loggingUtils.getLogger()).thenReturn(logger);

        //when
        Executable actual = () -> enricher.enrich(TestConstants.ORDER_NOTIFICATION_REFERENCE,
            ITEM_GROUP_PROCESSED_SEND);

        //then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        verify(logger).error("Failed to map order and item ready notification", exception,
            Collections.emptyMap());
    }

}
