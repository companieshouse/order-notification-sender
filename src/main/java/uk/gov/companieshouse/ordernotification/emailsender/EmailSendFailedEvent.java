package uk.gov.companieshouse.ordernotification.emailsender;

import uk.gov.companieshouse.ordernotification.eventmodel.EventSourceRetrievable;
import uk.gov.companieshouse.ordernotification.eventmodel.OrderIdentifiable;

import java.util.Objects;

public class EmailSendFailedEvent implements EventSourceRetrievable {

    private final OrderIdentifiable eventSource;

    public EmailSendFailedEvent(OrderIdentifiable eventSource) {
        this.eventSource = eventSource;
    }

    public OrderIdentifiable getEventSource() {
        return eventSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmailSendFailedEvent that = (EmailSendFailedEvent) o;
        return Objects.equals(eventSource, that.eventSource);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventSource);
    }
}
