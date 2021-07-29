package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryApi;

@Component
public class OrderMapperFactory {

    private final CertificateOrderNotificationMapper certificateOrderNotificationMapper;
    private final DocumentOrderNotificationMapper documentOrderNotificationMapper;
    private final MissingImageOrderNotificationMapper missingImageOrderNotificationMapper;

    @Autowired
    public OrderMapperFactory(CertificateOrderNotificationMapper certificateOrderNotificationMapper,
                              DocumentOrderNotificationMapper documentOrderNotificationMapper,
                              MissingImageOrderNotificationMapper missingImageOrderNotificationMapper) {
        this.certificateOrderNotificationMapper = certificateOrderNotificationMapper;
        this.documentOrderNotificationMapper = documentOrderNotificationMapper;
        this.missingImageOrderNotificationMapper = missingImageOrderNotificationMapper;
    }

    public OrdersApiMapper getOrderMapper(OrdersApi ordersApi) {
        Class<? extends BaseItemApi> itemClass = ordersApi.getItems().get(0).getClass();
        if (itemClass.equals(CertificateApi.class)) {
            return certificateOrderNotificationMapper;
        } else if (itemClass.equals(CertifiedCopyApi.class)){
            return documentOrderNotificationMapper;
        } else if (itemClass.equals(MissingImageDeliveryApi.class)){
            return missingImageOrderNotificationMapper;
        }  else {
            throw new IllegalArgumentException("Unhandled item class");
        }
    }
}
