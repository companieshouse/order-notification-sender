package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.FixedDelayStrategy;
import org.springframework.messaging.Message;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.consumer.orderreceived.RetryableErrorException;

/**
 * Consumes <code>item-group-processed-send</code> messages and notifies the application that it is
 * ready to be processed.
 */
@Service
public class ItemGroupProcessedSendConsumer {

    private final ItemGroupProcessedSendHandler itemGroupProcessedSendHandler;

    public ItemGroupProcessedSendConsumer(
        ItemGroupProcessedSendHandler itemGroupProcessedSendHandler) {
        this.itemGroupProcessedSendHandler = itemGroupProcessedSendHandler;
    }

    /**
     * Consumes a message from the <code>item-group-processed-send</code> topic.
     *
     * @param message A {@link Message message} containing an
     *                {@link ItemGroupProcessedSend entity}.
     */
    @KafkaListener(id = "#{'${kafka.topics.item-group-processed-send-group}'}",
            groupId = "#{'${kafka.topics.item-group-processed-send-group}'}",
            topics = "#{'${kafka.topics.item-group-processed-send}'}",
            autoStartup = "#{!${uk.gov.companieshouse.order-notification-sender.error-consumer}}",
            containerFactory = "kafkaItemGroupProcessedSendListenerContainerFactory")
    @RetryableTopic(
        attempts = "${kafka.topics.item-group-processed-send.max_attempts}",
        autoCreateTopics = "false",
        backoff = @Backoff(delayExpression = "${kafka.topics.item-group-processed-send.backoff_delay}"),
        dltTopicSuffix = "-error",
        dltStrategy = DltStrategy.FAIL_ON_ERROR,
        fixedDelayTopicStrategy = FixedDelayStrategy.SINGLE_TOPIC,
        include = RetryableErrorException.class
    )
    public void processItemGroupProcessedSend(Message<ItemGroupProcessedSend> message) {
        itemGroupProcessedSendHandler.handleMessage(message);
    }

}
