package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.messaging.Message;
import uk.gov.companieshouse.orders.OrderReceivedNotificationRetry;

public class OrderReceivedNotificationRetryFacade extends MessageFacade<OrderReceivedNotificationRetry> {

    public OrderReceivedNotificationRetryFacade(Message<OrderReceivedNotificationRetry> message) {
        super(message);
    }

    @Override
    public String getOrderUri() {
        return getMessage().getPayload().getOrder().getOrderUri();
    }

    @Override
    public int getRetries() {
        return getMessage().getPayload().getAttempt();
    }
}
