package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.orders.OrderReceived;

@ExtendWith(MockitoExtension.class)
public class OrderReceivedLoggerTest {
    @Mock
    private Message<OrderReceived> message;

    @Mock
    private Logger logger;

    @Mock
    private LoggingUtils loggingUtils;

    @InjectMocks
    private OrderReceivedLogger orderReceivedLogger;

    @Test
    void testCorrectlyLogsMessageReceived() {
        //given
        OrderReceived orderReceived = new OrderReceived("order-uri", 1);
        when(message.getPayload()).thenReturn(orderReceived);
        Map<String, Object> headersMap = Collections.singletonMap("kafka_receivedTopic", "kafka-topic");
        MessageHeaders messageHeaders = new MessageHeaders(headersMap);
        when(message.getHeaders()).thenReturn(messageHeaders);
        when(loggingUtils.getLogger()).thenReturn(logger);

        //then
        orderReceivedLogger.logMessageReceived(message);

        //when
        verify(loggingUtils).logIfNotNull(anyMap(), eq(LoggingUtils.ORDER_URI), eq("order-uri"));
        verify(logger).info(eq("'kafka-topic' message received"), anyMap());
    }

    @Test
    void testCorrectlyLogsMessageProcessed() {
        //given
        OrderReceived orderReceived = new OrderReceived("order-uri", 1);
        when(message.getPayload()).thenReturn(orderReceived);
        when(loggingUtils.getLogger()).thenReturn(logger);

        //then
        orderReceivedLogger.logMessageProcessed(message);

        //when
        verify(loggingUtils).logIfNotNull(anyMap(), eq(LoggingUtils.ORDER_URI), eq("order-uri"));
        verify(logger).info(eq("Order received message processing completed"), anyMap());
    }
}
