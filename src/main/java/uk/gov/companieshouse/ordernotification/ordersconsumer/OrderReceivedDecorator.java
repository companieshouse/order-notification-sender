package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.messaging.Message;
import uk.gov.companieshouse.orders.OrderReceived;

public class OrderReceivedDecorator extends TransformationDecorator<OrderReceived>{

    public OrderReceivedDecorator(Message<OrderReceived> message) {
        super(message);
    }

    @Override
    public String transform() {
        return getMessage().getPayload().getOrderUri();
    }

    @Override
    public int getRetries() {
        return 0;
    }
}
