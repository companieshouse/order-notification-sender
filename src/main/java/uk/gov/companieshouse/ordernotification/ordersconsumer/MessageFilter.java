package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.messaging.Message;

public interface MessageFilter<T> {
    boolean include(Message<T> message);
}