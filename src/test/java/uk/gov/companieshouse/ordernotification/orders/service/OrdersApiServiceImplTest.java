package uk.gov.companieshouse.ordernotification.orders.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.order.PrivateOrderResourceHandler;
import uk.gov.companieshouse.api.handler.order.request.OrdersGet;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OrdersApiServiceImplTest {
    private static final String ORDER_URL = "/orders/1234";
    private static final String ORDER_URL_INCORRECT = "/bad-orders/url";

    @InjectMocks
    OrdersApiServiceImpl serviceUnderTest;

    @Mock
    ApiResponse<OrdersApi> ordersResponse;

    @Mock
    OrdersApi ordersApi;

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
        //given
        when(apiClient.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateOrderResourceHandler()).thenReturn(privateOrderResourceHandler);
        when(privateOrderResourceHandler.getOrder(ORDER_URL)).thenReturn(ordersGet);
        when(ordersGet.execute()).thenReturn(ordersResponse);
        when(ordersResponse.getData()).thenReturn(ordersApi);

        //when
        OrdersApi actual = serviceUnderTest.getOrderData(ORDER_URL);

        //then
        assertThat(actual, is(ordersApi));
    }

    @Test
    void getOrderDataThrowsServiceExceptionForIncorrectUri() throws ApiErrorResponseException, URIValidationException {
        //given
        when(apiClient.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateOrderResourceHandler()).thenReturn(privateOrderResourceHandler);
        when(privateOrderResourceHandler.getOrder(anyString())).thenReturn(ordersGet);
        when(ordersGet.execute()).thenThrow(URIValidationException.class);
        Assertions.assertThrows(OrdersServiceException.class, () -> serviceUnderTest.getOrderData(ORDER_URL_INCORRECT));
    }

    @Test
    void getOrderDataThrowsServiceExceptionForNon200Response() throws ApiErrorResponseException, URIValidationException {
        //given
        when(apiClient.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateOrderResourceHandler()).thenReturn(privateOrderResourceHandler);
        when(privateOrderResourceHandler.getOrder(anyString())).thenReturn(ordersGet);
        when(ordersGet.execute()).thenThrow(ApiErrorResponseException.class);
        OrdersResponseException exception = Assertions.assertThrows(OrdersResponseException.class, () -> serviceUnderTest.getOrderData(ORDER_URL_INCORRECT));
        assertEquals("Error returned by Orders API for order URL: " + ORDER_URL_INCORRECT, exception.getMessage());
    }
}
