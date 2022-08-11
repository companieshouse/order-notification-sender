package uk.gov.companieshouse.ordernotification.orders.service;

import uk.gov.companieshouse.api.model.order.OrdersApi;

public interface OrdersApiWrappable {
    OrdersApi getOrdersApi();
}
