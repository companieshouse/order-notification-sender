package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.DeliveryMethodApi;

import java.util.Map;

@Component("deliveryMethodMapper")
public class DeliveryMethodMapper {

    private Map<DeliveryMethodApi, String> mappings;

    public DeliveryMethodMapper(@Qualifier("deliveryMethodMappings") Map<DeliveryMethodApi, String> mappings) {
        this.mappings = mappings;
    }

    public String mapDeliveryMethod(DeliveryMethodApi deliveryMethod) {
        return mappings.get(deliveryMethod);
    }
}
