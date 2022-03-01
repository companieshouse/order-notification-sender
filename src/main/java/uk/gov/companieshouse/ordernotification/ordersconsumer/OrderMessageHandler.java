package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEventFactory;
import uk.gov.companieshouse.orders.OrderReceived;

@Service
public class OrderMessageHandler implements ApplicationEventPublisherAware {

    private MessageLogger messageLogger;
    private SendOrderNotificationEventFactory eventFactory;
    private ApplicationEventPublisher applicationEventPublisher;

    public OrderMessageHandler(final MessageLogger messageLogger,
                               final SendOrderNotificationEventFactory eventFactory) {
        this.messageLogger = messageLogger;
        this.eventFactory = eventFactory;
    }

    /**
     * Handles processing of received message.
     *
     * @param message received
     */
    public void handleMessage(Message<OrderReceived> message) {
        messageLogger.logMessageReceived(message);

        applicationEventPublisher.publishEvent(eventFactory.createEvent(message));

        messageLogger.logMessageProcessed(message);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}