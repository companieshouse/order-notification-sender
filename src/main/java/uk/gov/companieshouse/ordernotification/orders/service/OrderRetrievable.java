package uk.gov.companieshouse.ordernotification.orders.service;

public interface OrderRetrievable {
    OrdersApiWrappable getOrderData(String orderUri) throws OrdersResponseException;
}
