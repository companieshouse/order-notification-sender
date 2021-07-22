package uk.gov.companieshouse.ordernotification.ordersconsumer;

public class OrderProcessingException extends RuntimeException {
    public OrderProcessingException() { super("Order processing failed."); }
}
