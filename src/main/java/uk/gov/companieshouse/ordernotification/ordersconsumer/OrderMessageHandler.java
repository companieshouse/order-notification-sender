package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEventFactory;
import uk.gov.companieshouse.orders.OrderReceived;

@Service
public class OrderMessageHandler implements ApplicationEventPublisherAware {

    private final SendOrderNotificationEventFactory eventFactory;
    private final MessageFilter<OrderReceived> messageFilter;
    private ApplicationEventPublisher applicationEventPublisher;

    public OrderMessageHandler(SendOrderNotificationEventFactory eventFactory,
                               MessageFilter<OrderReceived> messageFilter) {
        this.eventFactory = eventFactory;
        this.messageFilter = messageFilter;
    }

    /**
     * Handles processing of received message.
     *
     * @param message received
     */
    public void handleMessage(Message<OrderReceived> message) {
        if (messageFilter.include(message)) {
            applicationEventPublisher.publishEvent(eventFactory.createEvent(message));
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}