package uk.gov.companieshouse.ordernotification.orders.service;

import java.util.Map;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.order.PrivateOrderResourceHandler;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrdersApiDetails;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrdersApiDetailsBuilder;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

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
    public OrdersApiDetails getOrderData(String orderUri) {
        Map<String, Object> logMap = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, orderUri);
        try {
            InternalApiClient internalApiClient = apiClient.getInternalApiClient();
            PrivateOrderResourceHandler privateOrderResourceHandler = internalApiClient.privateOrderResourceHandler();
            ApiResponse<OrdersApi> response = privateOrderResourceHandler.getOrder(orderUri)
                    .execute();

            OrdersApi ordersApi = response.getData();
            loggingUtils.getLogger().debug("Order data returned from API client", logMap);
            return OrdersApiDetailsBuilder.newBuilder()
                    .withOrdersApi(ordersApi)
                    .build();
        } catch (ApiErrorResponseException exception) {
            String message = String.format("Order URI %s, API exception %s, HTTP status %d",
                    orderUri,
                    exception.getMessage(),
                    exception.getStatusCode()
            );
            if (exception.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                loggingUtils.getLogger().info(message, logMap);
                throw new OrdersResponseException(message, exception);
            } else {
                loggingUtils.getLogger().error(message, exception);
                throw new OrdersServiceException(message, exception);
            }
        } catch (URIValidationException exception) {
            loggingUtils.getLogger().error("Unrecognised URI pattern", exception, logMap);
            throw new OrdersServiceException("Unrecognised uri pattern: " + orderUri, exception);
        }
    }
}
