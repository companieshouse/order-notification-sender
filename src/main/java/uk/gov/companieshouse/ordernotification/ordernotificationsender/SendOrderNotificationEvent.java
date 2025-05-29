package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import java.util.Objects;
import uk.gov.companieshouse.ordernotification.eventmodel.OrderIdentifiable;

/**
 * Raised when an order is ready to be processed.
 */
public class SendOrderNotificationEvent implements OrderIdentifiable {

    private final String orderReference;
    private final int retryCount;


    public SendOrderNotificationEvent(String orderReference, int retryCount) {
        this.orderReference = orderReference;
        this.retryCount = retryCount;
    }

    public String getOrderURI() {
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SendOrderNotificationEvent {");
        sb.append("    orderReference: ").append(this.toIndentedString(this.orderReference));
        sb.append("    retryCount: ").append(this.toIndentedString(this.retryCount));
        sb.append("}");
        return sb.toString();
    }

    /**
     * Converts the given object to string with each line indented by 4 spaces
     * (except the first line).
     *
     * @param o The object to convert to string.
     * @return The indented string representation of the object.
     */
    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

}
