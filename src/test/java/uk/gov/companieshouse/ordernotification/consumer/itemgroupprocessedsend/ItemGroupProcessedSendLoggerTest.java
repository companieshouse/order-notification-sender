package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_GROUP_PROCESSED_SEND;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_ID;

import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

@ExtendWith(MockitoExtension.class)
class ItemGroupProcessedSendLoggerTest {
    @Mock
    private Message<ItemGroupProcessedSend> message;

    @Mock
    private Logger logger;

    @Mock
    private LoggingUtils loggingUtils;

    @InjectMocks
    private ItemGroupProcessedSendLogger itemGroupProcessedSendLogger;

    @BeforeEach
    void setUp() {
        // (given)
        when(message.getPayload()).thenReturn(ITEM_GROUP_PROCESSED_SEND);
        final Map<String, Object> headersMap = Collections.singletonMap("kafka_receivedTopic", "kafka-topic");
        final MessageHeaders messageHeaders = new MessageHeaders(headersMap);
        when(message.getHeaders()).thenReturn(messageHeaders);
        when(loggingUtils.getLogger()).thenReturn(logger);
    }

    @Test
    void testCorrectlyLogsMessageReceived() {

        // when
        itemGroupProcessedSendLogger.logMessageReceived(message);

        // then
        verify(loggingUtils).logIfNotNull(anyMap(), eq(LoggingUtils.ITEM_ID), eq(ITEM_ID));
        verify(logger).info(eq("'kafka-topic' message received"), anyMap());
    }

    @Test
    void testCorrectlyLogsMessageProcessed() {

        // when
        itemGroupProcessedSendLogger.logMessageProcessed(message);

        // then
        verify(loggingUtils).logIfNotNull(anyMap(), eq(LoggingUtils.ITEM_ID), eq(ITEM_ID));
        verify(logger).info(eq("'kafka-topic' message processing completed"), anyMap());
    }
}
