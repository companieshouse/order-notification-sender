package uk.gov.companieshouse.ordernotification.orders.service;

import uk.gov.companieshouse.api.model.order.OrdersApi;

public class OrdersApiWrapper implements OrdersApiWrappable {
    private final OrdersApi ordersApi;

    public OrdersApiWrapper(OrdersApi ordersApi) {
        this.ordersApi = ordersApi;
    }

    @Override
    public OrdersApi getOrdersApi() {
        return ordersApi;
    }
}
