package uk.gov.companieshouse.ordernotification.ordersconsumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEventFactory;
import uk.gov.companieshouse.orders.OrderReceived;

@ExtendWith(MockitoExtension.class)
class OrderMessageHandlerTest {

    @Mock
    private Message<OrderReceived> message;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private MessageLogger messageLogger;

    @Mock
    private SendOrderNotificationEventFactory eventFactory;

    @InjectMocks
    private OrderMessageHandler orderMessageHandler;

    @Test
    void testHandleMessagePublishesEvent() {
        // given
        SendOrderNotificationEvent event = new SendOrderNotificationEvent("ORD" +
                "-12345-678", 1);
        when(eventFactory.createEvent(message)).thenReturn(event);
        // when
        orderMessageHandler.handleMessage(message);
        // then
        verify(applicationEventPublisher).publishEvent(event);
    }
}