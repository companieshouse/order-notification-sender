package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import java.util.Objects;

public class OrderEnrichmentFailedEvent {
    private final Object eventSource;

    public OrderEnrichmentFailedEvent(Object eventSource) {
        this.eventSource = eventSource;
    }

    public Object getEventSource() {
        return eventSource;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderEnrichmentFailedEvent)) return false;
        OrderEnrichmentFailedEvent that = (OrderEnrichmentFailedEvent) o;
        return Objects.equals(getEventSource(), that.getEventSource());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEventSource());
    }
}
