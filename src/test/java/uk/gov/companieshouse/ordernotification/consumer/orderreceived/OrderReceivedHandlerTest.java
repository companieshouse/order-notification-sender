package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.Message;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEventFactory;
import uk.gov.companieshouse.orders.OrderReceived;

@ExtendWith(MockitoExtension.class)
class OrderReceivedHandlerTest {

    @Mock
    private Message<OrderReceived> message;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @Mock
    private SendOrderNotificationEventFactory eventFactory;

    @InjectMocks
    private OrderReceivedHandler orderReceivedHandler;

    @BeforeEach
    void beforeEach() {
        orderReceivedHandler.setApplicationEventPublisher(applicationEventPublisher);
    }

    @Test
    void testHandleMessagePublishesEvent() {
        // given
        SendOrderNotificationEvent event = new SendOrderNotificationEvent("ORD-12345-678", 1);
        when(eventFactory.createEvent(message)).thenReturn(event);

        // when
        orderReceivedHandler.handleMessage(message);

        // then
        verify(applicationEventPublisher).publishEvent(event);
    }
}