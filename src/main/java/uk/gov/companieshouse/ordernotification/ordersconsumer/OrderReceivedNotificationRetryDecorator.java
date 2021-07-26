package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.messaging.Message;
import uk.gov.companieshouse.orders.OrderReceivedNotificationRetry;

public class OrderReceivedNotificationRetryDecorator extends TransformationDecorator<OrderReceivedNotificationRetry> {

    public OrderReceivedNotificationRetryDecorator(Message<OrderReceivedNotificationRetry> message) {
        super(message);
    }

    @Override
    public String transform() {
        return getMessage().getPayload().getOrder().getOrderUri();
    }

    @Override
    public int getRetries() {
        return getMessage().getPayload().getAttempt();
    }
}
