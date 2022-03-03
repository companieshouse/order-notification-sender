package uk.gov.companieshouse.ordernotification.orders.service;

import uk.gov.companieshouse.ordernotification.ordersconsumer.RetryableErrorException;

/**
 * Raised if an error occurs when fetching an order resource.
 */
public class OrdersResponseException extends RetryableErrorException {

    public OrdersResponseException(String message, Throwable e) {
        super(message, e);
    }
}
