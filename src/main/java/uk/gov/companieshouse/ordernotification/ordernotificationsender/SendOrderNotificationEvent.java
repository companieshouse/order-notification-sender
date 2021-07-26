package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import java.util.Objects;

public class SendOrderNotificationEvent {

    private final String orderReference;
    private final int retryCount;


    public SendOrderNotificationEvent(String orderReference, int retryCount) {
        this.orderReference = orderReference;
        this.retryCount = retryCount;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public int getRetryCount() {
        return retryCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SendOrderNotificationEvent that = (SendOrderNotificationEvent) o;
        return retryCount == that.retryCount &&
                Objects.equals(orderReference, that.orderReference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderReference, retryCount);
    }
}