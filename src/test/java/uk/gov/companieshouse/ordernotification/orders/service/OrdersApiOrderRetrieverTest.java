package uk.gov.companieshouse.ordernotification.orders.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
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
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class OrdersApiOrderRetrieverTest {
    private static final String ORDER_URL = "/orders/1234";
    private static final String ORDER_URL_INCORRECT = "/bad-orders/url";

    @InjectMocks
    OrdersApiOrderRetriever serviceUnderTest;

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

    @Mock
    private Logger logger;

    @Test
    void getOrderData() throws Exception {
        //given
        Map<String, Object> logMap = new HashMap<>();
        when(apiClient.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateOrderResourceHandler()).thenReturn(privateOrderResourceHandler);
        when(privateOrderResourceHandler.getOrder(ORDER_URL)).thenReturn(ordersGet);
        when(ordersGet.execute()).thenReturn(ordersResponse);
        when(ordersResponse.getStatusCode()).thenReturn(200);
        when(ordersResponse.getData()).thenReturn(ordersApi);
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(loggingUtils.createLogMap()).thenReturn(logMap);

        //when
        OrdersApi actual = serviceUnderTest.getOrderData(ORDER_URL);

        //then
        assertThat(actual, is(ordersApi));
        verify(logger).debug("Order data returned from API client", logMap);
    }

    @Test
    void getOrderDataThrowsServiceExceptionForIncorrectUri() throws ApiErrorResponseException, URIValidationException {
        //given
        Map<String, Object> logMap = new HashMap<>();
        when(apiClient.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateOrderResourceHandler()).thenReturn(privateOrderResourceHandler);
        when(privateOrderResourceHandler.getOrder(anyString())).thenReturn(ordersGet);
        when(ordersGet.execute()).thenThrow(URIValidationException.class);
        when(loggingUtils.getLogger()).thenReturn(logger);
        when(loggingUtils.createLogMap()).thenReturn(logMap);
        OrdersServiceException e = assertThrows(OrdersServiceException.class, () -> serviceUnderTest.getOrderData(ORDER_URL_INCORRECT));
        verify(logger).error(eq("Unrecognised URI pattern"), any(), eq(logMap));
    }

    @Test
    void getOrderDataThrowsServiceExceptionIfApiErrorResponseExceptionThrown() throws ApiErrorResponseException, URIValidationException {
        //given
        when(apiClient.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateOrderResourceHandler()).thenReturn(privateOrderResourceHandler);
        when(privateOrderResourceHandler.getOrder(anyString())).thenReturn(ordersGet);
        when(ordersGet.execute()).thenThrow(ApiErrorResponseException.class);
        OrdersResponseException exception = assertThrows(OrdersResponseException.class, () -> serviceUnderTest.getOrderData(ORDER_URL_INCORRECT));
        assertEquals("Error fetching data from Orders API", exception.getMessage());
    }

    @Test
    void getOrderDataThrowsServiceExceptionIfNon200ReturnedByOrdersApi() throws ApiErrorResponseException, URIValidationException {
        //given
        when(apiClient.getInternalApiClient()).thenReturn(internalApiClient);
        when(internalApiClient.privateOrderResourceHandler()).thenReturn(privateOrderResourceHandler);
        when(privateOrderResourceHandler.getOrder(anyString())).thenReturn(ordersGet);
        when(ordersGet.execute()).thenReturn(ordersResponse);
        when(ordersResponse.getStatusCode()).thenReturn(500);

        //when
        Executable actual = () -> serviceUnderTest.getOrderData(ORDER_URL);

        //then
        OrdersResponseException exception = assertThrows(OrdersResponseException.class, actual);
        assertEquals("Orders API returned status code 500", exception.getMessage());
    }
}
