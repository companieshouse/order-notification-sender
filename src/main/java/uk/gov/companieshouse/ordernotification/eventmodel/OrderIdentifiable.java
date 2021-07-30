package uk.gov.companieshouse.ordernotification.eventmodel;

public interface OrderIdentifiable {
    String getOrderReference();
    int getRetryCount();
}
