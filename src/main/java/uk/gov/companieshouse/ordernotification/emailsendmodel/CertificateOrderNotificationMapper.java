package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@Component
public class CertificateOrderNotificationMapper implements KindMapper {
    private final EmailConfiguration emailConfiguration;
    private final CertificateOrderModelFactory orderModelFactory;

    public CertificateOrderNotificationMapper(EmailConfiguration emailConfiguration,
                                              CertificateOrderModelFactory orderModelFactory) {
        this.emailConfiguration = emailConfiguration;
        this.orderModelFactory = orderModelFactory;
    }

    @Override
    public OrderDetails map(OrdersApiDetails ordersApiDetails) {
        return OrderDetailsBuilder.newBuilder()
                .withMessageId(emailConfiguration.getCertificate().getMessageId())
                .withMessageType(emailConfiguration.getCertificate().getMessageType())
                .withOrderModel(orderModelFactory.newInstance(ordersApiDetails))
                .build();
    }
}
