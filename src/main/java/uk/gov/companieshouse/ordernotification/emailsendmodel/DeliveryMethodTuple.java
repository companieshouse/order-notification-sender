package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.DeliveryMethodApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;

import java.util.Objects;

/**
 * Encapsulates the delivery method and the delivery timescale.
 */
public class DeliveryMethodTuple {

    private final DeliveryMethodApi deliveryMethod;
    private final DeliveryTimescaleApi deliveryTimescale;

    public DeliveryMethodTuple(DeliveryMethodApi deliveryMethod, DeliveryTimescaleApi deliveryTimescale) {
        this.deliveryMethod = deliveryMethod;
        this.deliveryTimescale = deliveryTimescale;
    }

    public DeliveryMethodApi getDeliveryMethod() {
        return deliveryMethod;
    }

    public DeliveryTimescaleApi getDeliveryTimescale() {
        return deliveryTimescale;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DeliveryMethodTuple)) {
            return false;
        }
        DeliveryMethodTuple that = (DeliveryMethodTuple) o;
        return getDeliveryMethod() == that.getDeliveryMethod() &&
                getDeliveryTimescale() == that.getDeliveryTimescale();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDeliveryMethod(), getDeliveryTimescale());
    }
}
