package uk.gov.companieshouse.ordernotification.orders.service;

public interface OrderRetrievable {
    OrdersApiDetails getOrderData(String orderUri) throws OrdersResponseException;
}
