package uk.gov.companieshouse.ordernotification.ordernotificationsender;

import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.orders.OrderReceived;

@Component
public class SendOrderNotificationEventFactory {

    public SendOrderNotificationEvent createEvent(Message<OrderReceived> message) {
        return new SendOrderNotificationEvent(message.getPayload().getOrderUri(),
                message.getPayload().getAttempt());
    }
}
