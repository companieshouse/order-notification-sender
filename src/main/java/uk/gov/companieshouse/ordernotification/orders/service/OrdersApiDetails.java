package uk.gov.companieshouse.ordernotification.orders.service;

import java.time.LocalDateTime;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemOptionsApi;

public interface OrdersApiDetails {
    String getKind();
    String getCompanyName();
    String getCompanyNumber();
    String getOrderEmail();
    String getOrderReference();
    String getTotalOrderCost();
    String getPaymentReference();
    LocalDateTime getOrderedAt();
    BaseItemApi getBaseItemApi();
    BaseItemOptionsApi getBaseItemOptions();
}
