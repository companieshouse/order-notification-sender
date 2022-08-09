package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE, proxyMode = ScopedProxyMode.INTERFACES)
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
