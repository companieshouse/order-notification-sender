package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.messaging.Message;

public abstract class MessageFacade<T> {

    private final Message<T> message;

    public MessageFacade(Message<T> message) {
        this.message = message;
    }

    public abstract String getOrderUri();

    public abstract int getRetries();

    public Message<T> getMessage() {
        return this.message;
    }
}
