package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@Component
class MissingImageOrderNotificationMapper implements KindMapper {

    private final MissingImageOrderModelFactory orderModelFactory;
    private final EmailConfiguration emailConfiguration;

    public MissingImageOrderNotificationMapper(MissingImageOrderModelFactory orderModelFactory,
                                               EmailConfiguration emailConfiguration) {
        this.orderModelFactory = orderModelFactory;
        this.emailConfiguration = emailConfiguration;
    }

    @Override
    public OrderDetails map(OrdersApiDetails ordersApi) {
        return OrderDetailsBuilder.newBuilder()
                .withMessageId(emailConfiguration.getMissingImage().getMessageId())
                .withMessageType(emailConfiguration.getMissingImage().getMessageType())
                .withOrderModel(orderModelFactory.newInstance(ordersApi))
                .build();
    }
}
