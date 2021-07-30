package uk.gov.companieshouse.ordernotification.orders.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.order.PrivateOrderResourceHandler;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.Map;

/**
 * Retrieves order data using the provided order reference number.
 */
@Service
class OrdersApiServiceImpl implements OrdersApiService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUtils.APPLICATION_NAMESPACE);
    
    private final ApiClient apiClient;
    private final LoggingUtils loggingUtils;

    @Autowired
    public OrdersApiServiceImpl(ApiClient apiClient, LoggingUtils loggingUtils) {
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
            OrdersApi ordersApi = privateOrderResourceHandler.getOrder(orderUri).execute().getData();
            LOGGER.info("Order data returned from API Client", logMap);
            return ordersApi;
        } catch(URIValidationException e) {
            throw new OrdersServiceException("Unrecognised uri pattern for "+orderUri);
        } catch (ApiErrorResponseException e) {
            throw new OrdersResponseException("Error returned by Orders API");
        }
    }
}
