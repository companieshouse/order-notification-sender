package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;

public interface OrderNotificationDataConvertable {
    void mapOrder(OrdersApi ordersApi);
    void mapCertificate(BaseItemApi certificate);
    void mapCertifiedCopy(BaseItemApi certifiedCopy);
    void mapMissingImageDelivery(BaseItemApi missingImageDelivery);
    OrderNotificationEmailData getEmailData();
}
