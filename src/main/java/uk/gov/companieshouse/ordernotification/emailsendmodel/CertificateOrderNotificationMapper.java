package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@Component
public class CertificateOrderNotificationMapper implements KindMapper {
    private final EmailConfiguration emailConfiguration;
    private final CertificateOptionsMapperFactory mapperFactory;

    public CertificateOrderNotificationMapper(EmailConfiguration emailConfiguration,
                                              CertificateOptionsMapperFactory mapperFactory) {
        this.emailConfiguration = emailConfiguration;
        this.mapperFactory = mapperFactory;
    }

    @Override
    public OrderDetails map(OrdersApiDetails ordersApiDetails) {
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) ordersApiDetails.getBaseItemOptions();
        return OrderDetailsBuilder.newBuilder()
                .withMessageId(emailConfiguration.getCertificate().getMessageId())
                .withMessageType(emailConfiguration.getCertificate().getMessageType())
                .withOrderModel(mapperFactory
                        .getCertificateOptionsMapper(itemOptions.getCompanyType())
                        .generateEmailData(ordersApiDetails))
                .build();
    }
}
