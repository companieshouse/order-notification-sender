package uk.gov.companieshouse.ordernotification.ordersapi.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.handler.order.PrivateOrderResourceHandler;
import uk.gov.companieshouse.api.handler.order.request.OrdersGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordersapi.model.OrderData;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrdersApiClientServiceTest {
    private static final String ORDER_URL = "/orders/1234";
    private static final String ORDER_URL_INCORRECT = "/bad-orders/url";
    private static final String ORDER_ETAG = "abCxYz0324";

    @InjectMocks
    OrdersApiClientService serviceUnderTest;

    @Mock
    ApiResponse<OrdersApi> ordersResponse;

    @Mock
    OrdersApi ordersApi;

    @Mock
    OrdersApiToOrderDataMapper ordersApiToOrderDataMapper;

    @Mock
    ApiClient apiClient;

    @Mock
    InternalApiClient internalApiClient;

    @Mock
    PrivateOrderResourceHandler privateOrderResourceHandler;

    @Mock
    OrdersGet ordersGet;

    @Mock
    LoggingUtils loggingUtils;

    @Test
    void getOrderData() throws Exception {
        final OrderData expectedOrderData = new OrderData();
        expectedOrderData.setEtag(ORDER_ETAG);

        // Given OrdersApi returns valid details
        when(apiClient.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateOrderResourceHandler()).thenReturn(privateOrderResourceHandler);
        when(privateOrderResourceHandler.getOrder(ORDER_URL)).thenReturn(ordersGet);
        when(ordersGet.execute()).thenReturn(ordersResponse);
        when(ordersResponse.getData()).thenReturn(ordersApi);
        when(ordersApiToOrderDataMapper.ordersApiToOrderData(ordersApi)).thenReturn(expectedOrderData);

        // When & Then
        OrderData actualOrderData = serviceUnderTest.getOrderData(ORDER_URL);
        assertThat(actualOrderData.getEtag(), is(expectedOrderData.getEtag()));
        verify(ordersApiToOrderDataMapper, times(1)).ordersApiToOrderData(ordersApi);
    }

    @Test
    void getOrderDataThrowsServiceExceptionForIncorrectUri() {
        Assertions.assertThrows(ServiceException.class, () -> serviceUnderTest.getOrderData(ORDER_URL_INCORRECT));
    }
}
