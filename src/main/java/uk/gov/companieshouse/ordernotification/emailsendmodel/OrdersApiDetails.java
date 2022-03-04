package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.List;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemOptionsApi;

public interface OrdersApiDetails {
    OrdersApi getOrdersApi();
    BaseItemApi getBaseItemApi();
    String getKind();
    List<BaseItemApi> getItems();
    BaseItemOptionsApi getBaseItemOptions();
    String getReference();
}
