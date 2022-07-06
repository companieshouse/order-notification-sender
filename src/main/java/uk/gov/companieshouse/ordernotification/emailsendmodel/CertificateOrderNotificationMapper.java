package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@Component
public class CertificateOrderNotificationMapper implements OrderKindMapper {
    private final CertificateOptionsMapperFactory mapperFactory;
    private final CertificateOrderMessageTypeFactory messageTypeFactory;

    public CertificateOrderNotificationMapper(CertificateOptionsMapperFactory mapperFactory,
                                              CertificateOrderMessageTypeFactory messageTypeFactory) {
        this.mapperFactory = mapperFactory;
        this.messageTypeFactory = messageTypeFactory;
    }

    @Override
    public OrderDetails map(OrdersApiDetails ordersApiDetails) {
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) ordersApiDetails.getItemOptions();
        EmailDataConfiguration messageConfiguration = messageTypeFactory.getMessageConfiguration(itemOptions);
        return OrderDetailsBuilder.newBuilder()
                .withMessageId(messageConfiguration.getMessageId())
                .withMessageType(messageConfiguration.getMessageType())
                .withOrderModel(mapperFactory
                        .getCertificateOptionsMapper(itemOptions.getCompanyType())
                        .generateEmailData(ordersApiDetails))
                .build();
    }
}
