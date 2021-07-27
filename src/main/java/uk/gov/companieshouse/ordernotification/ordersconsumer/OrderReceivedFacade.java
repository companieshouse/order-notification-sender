package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.messaging.Message;
import uk.gov.companieshouse.orders.OrderReceived;

public class OrderReceivedFacade extends MessageFacade<OrderReceived> {

    public OrderReceivedFacade(Message<OrderReceived> message) {
        super(message);
    }

    @Override
    public String getOrderUri() {
        return getMessage().getPayload().getOrderUri();
    }

    @Override
    public int getRetries() {
        return 0;
    }
}
