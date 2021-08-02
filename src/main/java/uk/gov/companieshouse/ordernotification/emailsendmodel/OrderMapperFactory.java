package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * Return a subclass of {@link OrdersApiMapper} based on the item type of an order resource.
 */
@Component
public class OrderMapperFactory {

    private final Map<String, OrdersApiMapper> ordersApiMappers;

    @Autowired
    public OrderMapperFactory(@Qualifier("ordersApiMappers") Map<String, OrdersApiMapper> ordersApiMappers) {
        this.ordersApiMappers = ordersApiMappers;
    }

    /**
     * Return a subclass of {@link OrdersApiMapper} based on the item type of an order resource.
     *
     * @param kind The kind of order that has been fetched from orders API.
     * @return An {@link OrdersApiMapper} instance for the item type of the order.
     */
    public OrdersApiMapper getOrderMapper(String kind) {
        return Optional.ofNullable(ordersApiMappers.get(kind))
                .orElseThrow(() -> new IllegalArgumentException("Unhandled item kind"));
    }
}
