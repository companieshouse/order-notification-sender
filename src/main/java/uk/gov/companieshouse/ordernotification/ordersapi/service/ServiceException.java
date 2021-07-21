package uk.gov.companieshouse.ordernotification.ordersapi.service;

public class ServiceException extends RuntimeException {

    public ServiceException(String message) {
        super(message);
    }

}
