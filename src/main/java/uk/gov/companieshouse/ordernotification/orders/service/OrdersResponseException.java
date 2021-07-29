package uk.gov.companieshouse.ordernotification.orders.service;

public class OrdersResponseException extends Exception {

    public OrdersResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
