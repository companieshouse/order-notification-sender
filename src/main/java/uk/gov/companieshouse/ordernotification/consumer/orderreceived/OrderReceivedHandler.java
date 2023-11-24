package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEventFactory;
import uk.gov.companieshouse.orders.OrderReceived;

@Service
public class OrderReceivedHandler implements ApplicationEventPublisherAware {

    private final SendOrderNotificationEventFactory eventFactory;
    private ApplicationEventPublisher applicationEventPublisher;

    public OrderReceivedHandler(SendOrderNotificationEventFactory eventFactory) {
        this.eventFactory = eventFactory;
    }

    /**
     * Handles processing of received message.
     *
     * @param message received
     */
    public void handleMessage(Message<OrderReceived> message) {
        applicationEventPublisher.publishEvent(eventFactory.createEvent(message));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}