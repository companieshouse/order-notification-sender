package uk.gov.companieshouse.ordernotification.orders.service;

import uk.gov.companieshouse.api.model.order.OrdersApi;

public interface OrderRetrievable {
    OrdersApi getOrderData(String orderUri) throws OrdersResponseException;
}
