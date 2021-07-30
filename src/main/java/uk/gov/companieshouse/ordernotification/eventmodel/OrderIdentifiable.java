package uk.gov.companieshouse.ordernotification.eventmodel;

/**
 * Identifies an order by reference number and number of retries.
 */
public interface OrderIdentifiable {
    String getOrderURL();
    int getRetryCount();
}
