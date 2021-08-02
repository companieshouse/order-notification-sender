package uk.gov.companieshouse.ordernotification.eventmodel;

/**
 * An event that refers to another event.
 */
public interface EventSourceRetrievable {
    OrderIdentifiable getEventSource();
}
