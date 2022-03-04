package uk.gov.companieshouse.ordernotification.orders.service;

import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemOptionsApi;

public interface OrdersApiDetails {
    OrdersApi getOrdersApi();

    BaseItemApi getBaseItemApi();

    String getKind();

    BaseItemOptionsApi getBaseItemOptions();

    String getReference();
}
