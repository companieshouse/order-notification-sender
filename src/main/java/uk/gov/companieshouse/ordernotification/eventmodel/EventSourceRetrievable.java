package uk.gov.companieshouse.ordernotification.eventmodel;

public interface EventSourceRetrievable {
    OrderIdentifiable getEventSource();
}
