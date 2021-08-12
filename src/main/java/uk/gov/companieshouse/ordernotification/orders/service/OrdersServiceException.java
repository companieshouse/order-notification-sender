package uk.gov.companieshouse.ordernotification.orders.service;

/**
 * Raised if an unrecoverable error occurs when trying to fetch an order resource.
 */
public class OrdersServiceException extends RuntimeException {

    public OrdersServiceException(String message) {
        super(message);
    }

    public OrdersServiceException(Throwable cause) {
        super(cause);
    }

    public OrdersServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
