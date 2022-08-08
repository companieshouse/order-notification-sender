package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;

public class OrderNotificationEmailDataConverter implements OrderNotificationDataConvertable {

    private OrderNotificationEmailData emailData;

    @Override
    public void mapOrder(OrdersApi ordersApi) {

    }

    @Override
    public void mapCertificate(BaseItemApi certificate) {

    }

    @Override
    public void mapCertifiedCopy(BaseItemApi certifiedCopy) {

    }

    @Override
    public void mapMissingImageDelivery(BaseItemApi missingImageDelivery) {

    }

    @Override
    public OrderNotificationEmailData getEmailData() {
        return emailData;
    }
}
