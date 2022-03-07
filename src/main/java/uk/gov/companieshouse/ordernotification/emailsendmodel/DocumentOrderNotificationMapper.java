package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@Component
class DocumentOrderNotificationMapper implements OrderKindMapper {

    private final EmailConfiguration emailConfiguration;
    private final DocumentOrderDetailsMapper orderModelFactory;

    public DocumentOrderNotificationMapper(EmailConfiguration emailConfiguration,
                                           DocumentOrderDetailsMapper orderModelFactory) {
        this.emailConfiguration = emailConfiguration;
        this.orderModelFactory = orderModelFactory;
    }

    @Override
    public OrderDetails map(OrdersApiDetails ordersApiDetails) {
        return OrderDetailsBuilder.newBuilder()
                .withMessageId(emailConfiguration.getDocument().getMessageId())
                .withMessageType(emailConfiguration.getDocument().getMessageType())
                .withOrderModel(orderModelFactory.map(ordersApiDetails))
                .build();
    }
}
