package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@Component
class MissingImageOrderNotificationMapper implements OrderKindMapper {

    private final MissingImageOrderDetailsMapper orderModelFactory;
    private final EmailConfiguration emailConfiguration;

    public MissingImageOrderNotificationMapper(MissingImageOrderDetailsMapper orderModelFactory,
                                               EmailConfiguration emailConfiguration) {
        this.orderModelFactory = orderModelFactory;
        this.emailConfiguration = emailConfiguration;
    }

    @Override
    public OrderDetails map(OrdersApiDetails ordersApi) {
        return OrderDetailsBuilder.newBuilder()
                .withMessageId(emailConfiguration.getMissingImage().getMessageId())
                .withMessageType(emailConfiguration.getMissingImage().getMessageType())
                .withOrderModel(orderModelFactory.map(ordersApi))
                .build();
    }
}
