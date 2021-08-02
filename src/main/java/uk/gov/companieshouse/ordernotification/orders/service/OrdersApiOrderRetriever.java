package uk.gov.companieshouse.ordernotification.orders.service;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.order.PrivateOrderResourceHandler;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.Map;

/**
 * Retrieves order data using the provided order reference number.
 */
@Service
class OrdersApiOrderRetriever implements OrderRetrievable {
    
    private final ApiClient apiClient;
    private final LoggingUtils loggingUtils;

    @Autowired
    public OrdersApiOrderRetriever(ApiClient apiClient, LoggingUtils loggingUtils) {
        this.apiClient = apiClient;
        this.loggingUtils = loggingUtils;
    }

    @Override
    public OrdersApi getOrderData(String orderUri) throws OrdersResponseException {
        Map<String, Object> logMap = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, orderUri);
        try {
            InternalApiClient internalApiClient = apiClient.getInternalApiClient();
            PrivateOrderResourceHandler privateOrderResourceHandler = internalApiClient.privateOrderResourceHandler();
            ApiResponse<OrdersApi> response = privateOrderResourceHandler.getOrder(orderUri).execute();
            if(response.getStatusCode() != HttpStatus.SC_OK) {
                throw new OrdersResponseException("Orders API returned status code " + response.getStatusCode());
            } else {
                OrdersApi ordersApi = privateOrderResourceHandler.getOrder(orderUri).execute().getData();
                loggingUtils.getLogger().debug("Order data returned from API client", logMap);
                return ordersApi;
            }
        } catch(URIValidationException e) {
            loggingUtils.getLogger().error("Unrecognised URI pattern", e, logMap);
            throw new OrdersServiceException("Unrecognised uri pattern: "+orderUri, e);
        } catch (ApiErrorResponseException e) {
            throw new OrdersResponseException("Error fetching data from Orders API", e);
        }
    }
}
