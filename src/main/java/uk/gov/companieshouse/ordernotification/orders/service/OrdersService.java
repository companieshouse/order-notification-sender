package uk.gov.companieshouse.ordernotification.orders.service;

import uk.gov.companieshouse.ordernotification.orders.model.OrderData;

public interface OrdersService {
    OrderData getOrderData(String orderUri) throws Exception;
}
