package uk.gov.companieshouse.ordernotification.orders.service;

import uk.gov.companieshouse.api.model.order.OrdersApi;

public interface OrdersApiService {
    OrdersApi getOrderData(String orderUri) throws OrdersResponseException;
}
