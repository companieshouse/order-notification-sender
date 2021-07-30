package uk.gov.companieshouse.ordernotification.orders.service;

/**
 * Raised if an error occurs when fetching an order resource.
 */
public class OrdersResponseException extends Exception {

    public OrdersResponseException(String message) {
        super(message);
    }
}
