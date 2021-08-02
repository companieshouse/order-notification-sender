package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import uk.gov.companieshouse.ordernotification.eventmodel.EventSourceRetrievable;
import uk.gov.companieshouse.ordernotification.eventmodel.OrderIdentifiable;

import java.util.Objects;

/**
 * Raised if an error occurs when enriching an order with an order resource.
 */
public class OrderEnrichmentFailedEvent implements EventSourceRetrievable {

    private final OrderIdentifiable eventSource;

    public OrderEnrichmentFailedEvent(OrderIdentifiable eventSource) {
        this.eventSource = eventSource;
    }

    @Override
    public OrderIdentifiable getEventSource() {
        return eventSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderEnrichmentFailedEvent)) {
            return false;
        }
        OrderEnrichmentFailedEvent that = (OrderEnrichmentFailedEvent) o;
        return Objects.equals(getEventSource(), that.getEventSource());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventSource());
    }
}
