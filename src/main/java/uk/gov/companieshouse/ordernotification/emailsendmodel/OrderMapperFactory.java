package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;

@Component
public class OrderMapperFactory {

    private final CertificateOrderNotificationMapper certificateOrderNotificationMapper;
    private final DocumentOrderNotificationMapper documentOrderNotificationMapper;

    @Autowired
    public OrderMapperFactory(CertificateOrderNotificationMapper certificateOrderNotificationMapper,
                              DocumentOrderNotificationMapper documentOrderNotificationMapper) {
        this.certificateOrderNotificationMapper = certificateOrderNotificationMapper;
        this.documentOrderNotificationMapper = documentOrderNotificationMapper;
    }

    public OrdersApiMapper getOrderMapper(OrdersApi ordersApi) {
        String kind = ordersApi.getItems().get(0).getKind();
        if ("item#certificate".equals(kind)) {
            return certificateOrderNotificationMapper;
        } else {
            return documentOrderNotificationMapper;
        }
    }
}
