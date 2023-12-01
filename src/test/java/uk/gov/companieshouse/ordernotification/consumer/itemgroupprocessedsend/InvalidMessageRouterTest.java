package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend.InvalidMessageRouter.INVALID_MESSAGE_TOPIC;
import static uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend.InvalidMessageRouter.MESSAGE_FLAGS;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_GROUP_PROCESSED_SEND;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.SAME_PARTITION_KEY;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;

@ExtendWith(MockitoExtension.class)
class InvalidMessageRouterTest {

    private InvalidMessageRouter invalidMessageRouter;

    @Mock
    private MessageFlags flags;

    @BeforeEach
    void setup() {
        invalidMessageRouter = new InvalidMessageRouter();
        final Map<String, Object> routerProperties = new HashMap<>();
        routerProperties.put(MESSAGE_FLAGS, flags);
        routerProperties.put(INVALID_MESSAGE_TOPIC, "invalid");
        invalidMessageRouter.configure(routerProperties);
    }

    @Test
    void testOnSendRoutesMessageToInvalidMessageTopicIfNonRetryableExceptionThrown() {
        // given
        ProducerRecord<String, ItemGroupProcessedSend> message =
            new ProducerRecord<>("main", SAME_PARTITION_KEY, ITEM_GROUP_PROCESSED_SEND);

        // when
        ProducerRecord<String, ItemGroupProcessedSend> actual = invalidMessageRouter.onSend(
            message);

        // then
        assertThat(actual, is(equalTo(
            new ProducerRecord<>("invalid", SAME_PARTITION_KEY, ITEM_GROUP_PROCESSED_SEND))));
    }

    @Test
    void testOnSendRoutesMessageToTargetTopicIfRetryableExceptionThrown() {
        // given
        ProducerRecord<String, ItemGroupProcessedSend> message =
            new ProducerRecord<>("main", SAME_PARTITION_KEY, ITEM_GROUP_PROCESSED_SEND);
        when(flags.isRetryable()).thenReturn(true);

        // when
        ProducerRecord<String, ItemGroupProcessedSend> actual = invalidMessageRouter.onSend(
            message);

        // then
        assertThat(actual, is(sameInstance(message)));
    }

}
