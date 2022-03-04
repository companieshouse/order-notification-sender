package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@Component
class DocumentOrderNotificationMapper implements KindMapper {

    private final EmailConfiguration emailConfiguration;
    private final DocumentOrderModelFactory orderModelFactory;

    public DocumentOrderNotificationMapper(EmailConfiguration emailConfiguration,
                                           DocumentOrderModelFactory orderModelFactory) {
        this.emailConfiguration = emailConfiguration;
        this.orderModelFactory = orderModelFactory;
    }

    @Override
    public OrderDetails map(OrdersApiDetails ordersApiDetails) {
        return OrderDetailsBuilder.newBuilder()
                .withMessageId(emailConfiguration.getDocument().getMessageId())
                .withMessageType(emailConfiguration.getDocument().getMessageType())
                .withOrderModel(orderModelFactory.newInstance(ordersApiDetails))
                .build();
    }
}
