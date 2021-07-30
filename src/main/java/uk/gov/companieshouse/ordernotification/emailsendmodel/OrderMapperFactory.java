package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;

import java.util.Map;
import java.util.Optional;

/**
 * Return a subclass of {@link OrdersApiMapper} based on the item type of an order resource.
 */
@Component
public class OrderMapperFactory {

    private final Map<Class<? extends BaseItemApi>, OrdersApiMapper> ordersApiMappers;

    @Autowired
    public OrderMapperFactory(Map<Class<? extends BaseItemApi>, OrdersApiMapper> ordersApiMappers) {
        this.ordersApiMappers = ordersApiMappers;
    }

    /**
     * Return a subclass of {@link OrdersApiMapper} based on the item type of an order resource.
     *
     * @param ordersApi The order resource that has been fetched from orders API.
     * @return An {@link OrdersApiMapper} instance for the item type of the order.
     */
    public OrdersApiMapper getOrderMapper(OrdersApi ordersApi) {
        return Optional.ofNullable(ordersApiMappers.get(ordersApi.getItems().get(0).getClass()))
                .orElseThrow(() -> new IllegalArgumentException("Unhandled item class"));
    }
}
