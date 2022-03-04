package uk.gov.companieshouse.ordernotification.orders.service;

import uk.gov.companieshouse.ordernotification.emailsendmodel.OrdersApiDetails;

public interface OrderRetrievable {
    OrdersApiDetails getOrderData(String orderUri) throws OrdersResponseException;
}
