package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.DeliveryMethodApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;

import java.util.Map;

/**
 * Maps {@link DeliveryMethodApi} objects to human readable strings.
 */
@Component
public class DeliveryMethodMapper {

    private Map<DeliveryMethodTuple, String> mappings;

    public DeliveryMethodMapper(@Qualifier("deliveryMethodMappings") Map<DeliveryMethodTuple, String> mappings) {
        this.mappings = mappings;
    }

    /**
     * Maps {@link DeliveryMethodApi} objects to human readable strings.
     *
     * @param deliveryMethod An enum of certificate types
     * @param timescale An enum of delivery timescales
     * @return A string representation of the type
     */
    public String mapDeliveryMethod(DeliveryMethodApi deliveryMethod, DeliveryTimescaleApi timescale) {
        return mappings.get(new DeliveryMethodTuple(deliveryMethod, timescale));
    }
}
