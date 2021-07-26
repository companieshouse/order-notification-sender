package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.messaging.Message;

public abstract class TransformationDecorator<T> {

    private Message<T> message;

    public TransformationDecorator(Message<T> message) {
        this.message = message;
    }

    public abstract String transform();

    public abstract int getRetries();

    public Message<T> getMessage() {
        return this.message;
    }
}
