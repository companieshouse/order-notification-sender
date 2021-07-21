package uk.gov.companieshouse.ordernotification.ordersapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.handler.order.PrivateOrderResourceHandler;
import uk.gov.companieshouse.api.handler.order.request.PrivateOrderURIPattern;
import uk.gov.companieshouse.api.handler.regex.URIValidator;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordersapi.model.OrderData;

import java.util.Map;

@Service
public class OrdersApiClientService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingUtils.APPLICATION_NAMESPACE);
    
    private final ApiClient apiClient;
    private final OrdersApiToOrderDataMapper ordersApiToOrderDataMapper;
    private final LoggingUtils loggingUtils;

    @Autowired
    public OrdersApiClientService(ApiClient apiClient, OrdersApiToOrderDataMapper ordersApiToOrderDataMapper, LoggingUtils loggingUtils) {
        this.apiClient = apiClient;
        this.loggingUtils = loggingUtils;
        this.ordersApiToOrderDataMapper = ordersApiToOrderDataMapper;
    }

    public OrderData getOrderData(String orderUri) throws Exception {
        Map<String, Object> logMap = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, orderUri);
        if (URIValidator.validate(PrivateOrderURIPattern.getOrdersPattern(), orderUri)) {
            InternalApiClient internalApiClient = apiClient.getInternalApiClient();
            PrivateOrderResourceHandler privateOrderResourceHandler = internalApiClient.privateOrderResourceHandler();
            OrdersApi ordersApi = privateOrderResourceHandler.getOrder(orderUri).execute().getData();

            LOGGER.info("Order data returned from API Client", logMap);
            return ordersApiToOrderDataMapper.ordersApiToOrderData(ordersApi);
        } else {
            LOGGER.error("Unrecognised uri pattern", logMap);
            throw new ServiceException("Unrecognised uri pattern for "+orderUri);
        }
    }
}
