package uk.gov.companieshouse.ordernotification.logging;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.GenericMessage;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingUtilsTest {

    private LoggingUtils loggingUtils;

    @Mock
    private Logger logger;

    @BeforeEach
    void setup() {
        loggingUtils = new LoggingUtils(logger);
    }

    @Test
    void testCreateLogMapReturnsMap() {
        //when
        Map<String, Object> actual = loggingUtils.createLogMap();

        //then
        assertEquals(Collections.emptyMap(), actual);
    }

    @Test
    void testCreateLogMapWithKafkaMessage() {
        //given
        Message message = new Message();
        message.setTopic(TestConstants.KAFKA_TOPIC);
        message.setPartition(2);
        message.setOffset(3L);

        //when
        Map<String, Object> actual = loggingUtils.createLogMapWithKafkaMessage(message);

        //then
        assertEquals(expectedMap(), actual);
    }

    @Test
    void testCreateLogMapWithAcknowledgedKafkaMessage() {
        //given
        RecordMetadata metadata = new RecordMetadata(
                new TopicPartition(TestConstants.KAFKA_TOPIC, 2),
                0L,
                3L,
                0L,
                0L,
                0,
                0
        );

        //when
        Map<String, Object> actual = loggingUtils.createLogMapWithAcknowledgedKafkaMessage(metadata);

        //then
        assertEquals(expectedMap(), actual);
    }

    @Test
    void testCreateLogMapWithOrderUri() {
        //when
        Map<String, Object> actual = loggingUtils.createLogMapWithOrderUri(TestConstants.ORDER_NOTIFICATION_REFERENCE);

        //then
        assertEquals(Collections.singletonMap(LoggingUtils.ORDER_URI, TestConstants.ORDER_NOTIFICATION_REFERENCE), actual);
    }

    @Test
    void testLogIfNotNullIfObjectNonNull() {
        //given
        Map<String, Object> logArgs = new HashMap<>();

        //when
        loggingUtils.logIfNotNull(logArgs, "key", "value");

        //then
        assertTrue(logArgs.containsKey("key"));
    }

    @Test
    void testLogIfNotNullSkipIfObjectNull() {
        //given
        Map<String, Object> logArgs = new HashMap<>();

        //when
        loggingUtils.logIfNotNull(logArgs, "key", "value");

        //then
        assertTrue(logArgs.containsKey("key"));
    }

    @Test
    void testLogWithOrderUri() {
        //when
        Map<String, Object> actual = loggingUtils.logWithOrderUri("logMessage", TestConstants.ORDER_NOTIFICATION_REFERENCE);

        //then
        assertEquals(TestConstants.ORDER_NOTIFICATION_REFERENCE, actual.get(LoggingUtils.ORDER_URI));
        verify(logger).debug("logMessage", actual);
    }

    @Test
    void testLogMessageWithOrderUri() {
        //given
        Message message = new Message();
        message.setTopic(TestConstants.KAFKA_TOPIC);
        message.setPartition(2);
        message.setOffset(3L);

        Map<String, Object> expectedMap = expectedMap();
        expectedMap.put(LoggingUtils.ORDER_URI, TestConstants.ORDER_NOTIFICATION_REFERENCE);

        //when
        Map<String, Object> actual = loggingUtils.logMessageWithOrderUri(message, "logMessage", TestConstants.ORDER_NOTIFICATION_REFERENCE);

        //then
        assertEquals(expectedMap, actual);
        verify(logger).debug("logMessage", actual);
    }

    @Test
    void testGetMessageHeadersAsMap() {
        //given
        org.springframework.messaging.Message<String> message = new GenericMessage<>("hello", expectedKafkaHeaders());
        Map<String, Object> expectedMap = expectedMap();
        expectedMap.put(LoggingUtils.KEY, "key");

        //when
        Map<String, Object> actual = loggingUtils.getMessageHeadersAsMap(message);

        //then
        assertEquals(expectedMap, actual);
    }

    private Map<String, Object> expectedMap() {
        Map<String, Object> result = new HashMap<>();
        result.put(LoggingUtils.TOPIC, TestConstants.KAFKA_TOPIC);
        result.put(LoggingUtils.PARTITION, 2);
        result.put(LoggingUtils.OFFSET, 3L);
        return result;
    }

    private Map<String, Object> expectedKafkaHeaders() {
        Map<String, Object> result = new HashMap<>();
        result.put(KafkaHeaders.RECEIVED_MESSAGE_KEY, "key");
        result.put(KafkaHeaders.RECEIVED_TOPIC, TestConstants.KAFKA_TOPIC);
        result.put(KafkaHeaders.RECEIVED_PARTITION_ID, 2);
        result.put(KafkaHeaders.OFFSET, 3L);
        return result;
    }
}
