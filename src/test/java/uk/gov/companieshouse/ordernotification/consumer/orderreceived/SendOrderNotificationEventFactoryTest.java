package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEvent;
import uk.gov.companieshouse.ordernotification.ordernotificationsender.SendOrderNotificationEventFactory;
import uk.gov.companieshouse.orders.OrderReceived;

@ExtendWith(MockitoExtension.class)
class SendOrderNotificationEventFactoryTest {

    @Mock
    private Message<OrderReceived> orderReceivedMessage;

    @InjectMocks
    private SendOrderNotificationEventFactory factory;

    @Test
    void testConstructEventFromOrderReceivedMessage() {
        //given
        OrderReceived orderReceived = new OrderReceived("order-uri", 1);
        when(orderReceivedMessage.getPayload()).thenReturn(orderReceived);

        //when
        SendOrderNotificationEvent result = factory.createEvent(orderReceivedMessage);

        //then
        assertThat(result, is(new SendOrderNotificationEvent("order-uri", 1)));
    }
}
