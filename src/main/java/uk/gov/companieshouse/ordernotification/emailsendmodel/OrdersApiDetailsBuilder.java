package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.List;
import java.util.Optional;
import uk.gov.companieshouse.api.model.order.AbstractOrderDataApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemOptionsApi;

public class OrdersApiDetailsBuilder {
    private OrdersApi ordersApi;

    private OrdersApiDetailsBuilder() {
    }

    public static OrdersApiDetailsBuilder newBuilder() {
        return new OrdersApiDetailsBuilder();
    }

    public OrdersApiDetailsBuilder withOrdersApi(OrdersApi ordersApi) {
        this.ordersApi = ordersApi;
        return this;
    }

    public OrdersApiDetails build() {
        return new OrdersApiDetailsObject(this);
    }

    private static class OrdersApiDetailsObject implements OrdersApiDetails {
        private final OrdersApi ordersApi;

        private OrdersApiDetailsObject(OrdersApiDetailsBuilder builder) {
            ordersApi = builder.ordersApi;
        }

        @Override
        public OrdersApi getOrdersApi() {
            return ordersApi;
        }

        @Override
        public String getKind() {
            return Optional.ofNullable(getBaseItemApi())
                    .map(BaseItemApi::getKind).orElse(null);
        }

        @Override
        public List<BaseItemApi> getItems() {
            return Optional.ofNullable(getOrdersApi())
                    .map(AbstractOrderDataApi::getItems).orElse(null);
        }

        @Override
        public BaseItemOptionsApi getBaseItemOptions() {
            return Optional.ofNullable(getBaseItemApi())
                    .map(BaseItemApi::getItemOptions)
                    .orElse(null);
        }

        @Override
        public BaseItemApi getBaseItemApi() {
            return Optional.ofNullable(getOrdersApi())
                    .map(AbstractOrderDataApi::getItems)
                    .map(baseItemApis -> baseItemApis.get(0))
                    .orElse(null);
        }

        @Override
        public String getReference() {
            return Optional.ofNullable(getOrdersApi())
                    .map(AbstractOrderDataApi::getReference)
                    .orElse(null);
        }
    }
}
