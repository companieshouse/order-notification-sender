package uk.gov.companieshouse.ordernotification.emailsender;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.messageproducer.MessageProducer;

/**
 * Handles an incoming {@link SendEmailEvent} by sending a message containing email data.
 */
@Service
public class EmailSendService implements ApplicationEventPublisherAware {

    private static final String EMAIL_SEND_TOPIC = "email-send";

    private final MessageProducer producer;
    private final LoggingUtils loggingUtils;
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public EmailSendService(MessageProducer producer, LoggingUtils loggingUtils) {
        this.producer = producer;
        this.loggingUtils = loggingUtils;
    }

    /**
     * Handles an incoming {@link SendEmailEvent} by sending a message containing email data. If an error
     * occurs when publishing the message then the error handler will be notified.
     *
     * @param event A {@link SendEmailEvent} object containing email data
     * @throws NonRetryableFailureException if a serialization error occurs or the producer is interrupted
     */
    @EventListener
    public void handleEvent(SendEmailEvent event) {
        Map<String, Object> logArgs = loggingUtils.createLogMap();
        loggingUtils.logIfNotNull(logArgs, LoggingUtils.ORDER_URI, event.getOrderURI());
        try {
            producer.sendMessage(event.getEmailModel(), event.getOrderURI(), EMAIL_SEND_TOPIC);
        } catch (SerializationException e) {
            loggingUtils.getLogger().error("Failed to serialise email data as avro", e, logArgs);
            throw new NonRetryableFailureException("Failed to serialise email data as avro", e);
        } catch (ExecutionException | TimeoutException e) {
            loggingUtils.getLogger().error("Error sending email data to Kafka", e, loggingUtils.createLogMap());
            applicationEventPublisher.publishEvent(new EmailSendFailedEvent(event));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            loggingUtils.getLogger().error("Interrupted", e, logArgs);
            throw new NonRetryableFailureException("Interrupted", e);
        }
    }

    /**
     * Handles an incoming {@link SendItemReadyEmailEvent} by sending a message containing email data.
     *
     * @param event A {@link SendItemReadyEmailEvent} object containing item ready email data
     * @throws NonRetryableFailureException if a serialization error occurs or the producer is interrupted
     */
    @EventListener
    public void handleEvent(final SendItemReadyEmailEvent event) throws InterruptedException {
        try {
            producer.sendMessage(event.getEmailModel(), event.getOrderURI(), EMAIL_SEND_TOPIC);
        } catch (SerializationException e) {
            final String error = "Failed to serialise email data as avro";
            loggingUtils.getLogger().error(error, e, getLogMap(event));
            throw new NonRetryableFailureException(error, e);
        } catch (ExecutionException | TimeoutException e) {
            final String error = "Error sending email data to Kafka";
            loggingUtils.getLogger().error(error, e, getLogMap(event));
            throw new SendItemReadyEmailException(error, e);
        } catch (InterruptedException e) {

            // We do NOT propagate the interrupt state to the container by
            // calling Thread.currentThread().interrupt().
            // See https://github.com/spring-projects/spring-kafka/discussions/1847.

            final String error = "Interrupted";
            loggingUtils.getLogger().error(error, e, getLogMap(event));
            throw e;
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    private Map<String, Object> getLogMap(final SendItemReadyEmailEvent event) {
        final Map<String, Object> logMap = new DataMap.Builder()
            .itemId(event.getItemId())
            .build()
            .getLogMap();
        loggingUtils.logIfNotNull(logMap, LoggingUtils.ORDER_URI, event.getOrderURI());
        return logMap;
    }

}
