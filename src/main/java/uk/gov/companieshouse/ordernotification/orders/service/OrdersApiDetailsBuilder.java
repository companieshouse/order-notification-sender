package uk.gov.companieshouse.ordernotification.orders.service;

import java.time.LocalDateTime;
import java.util.Optional;
import uk.gov.companieshouse.api.model.order.AbstractOrderDataApi;
import uk.gov.companieshouse.api.model.order.ActionedByApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemOptionsApi;

final class OrdersApiDetailsBuilder {
    private OrdersApi ordersApi;

    private OrdersApiDetailsBuilder() {
    }

    static OrdersApiDetailsBuilder newBuilder() {
        return new OrdersApiDetailsBuilder();
    }

    OrdersApiDetailsBuilder withOrdersApi(OrdersApi ordersApi) {
        this.ordersApi = ordersApi;
        return this;
    }

    OrdersApiDetails build() {
        return new OrdersApiDetailsObject(this);
    }

    private static class OrdersApiDetailsObject implements OrdersApiDetails {
        private final OrdersApi ordersApi;

        private OrdersApiDetailsObject(OrdersApiDetailsBuilder builder) {
            ordersApi = builder.ordersApi;
        }

        private OrdersApi getOrdersApi() {
            return ordersApi;
        }

        @Override
        public String getKind() {
            return Optional.ofNullable(getBaseItemApi())
                    .map(BaseItemApi::getKind).orElse(null);
        }

        @Override
        public BaseItemOptionsApi getItemOptions() {
            return Optional.ofNullable(getBaseItemApi())
                    .map(BaseItemApi::getItemOptions)
                    .orElse(null);
        }

        @Override
        public String getOrderEmail() {
            return Optional.ofNullable(getOrdersApi())
                    .map(OrdersApi::getOrderedBy).map(
                    ActionedByApi::getEmail)
                    .orElse(null);
        }

        @Override
        public String getOrderReference() {
            return Optional.ofNullable(getOrdersApi())
                    .map(OrdersApi::getReference)
                    .orElse(null);
        }

        @Override
        public String getCompanyName() {
            return Optional.ofNullable(getBaseItemApi())
                    .map(BaseItemApi::getCompanyName)
                    .orElse(null);
        }

        @Override
        public String getCompanyNumber() {
            return Optional.ofNullable(getBaseItemApi())
                    .map(BaseItemApi::getCompanyNumber)
                    .orElse(null);
        }

        @Override
        public String getTotalOrderCost() {
            return Optional.ofNullable(getOrdersApi())
                    .map(OrdersApi::getTotalOrderCost)
                    .orElse(null);
        }

        @Override
        public String getPaymentReference() {
            return Optional.ofNullable(getOrdersApi())
                    .map(OrdersApi::getPaymentReference)
                    .orElse(null);
        }

        @Override
        public LocalDateTime getOrderedAt() {
            return Optional.ofNullable(getOrdersApi())
                    .map(OrdersApi::getOrderedAt)
                    .orElse(null);
        }

        private BaseItemApi getBaseItemApi() {
            return Optional.ofNullable(getOrdersApi())
                    .map(AbstractOrderDataApi::getItems)
                    .map(baseItemApis -> baseItemApis.get(0))
                    .orElse(null);
        }
    }
}
