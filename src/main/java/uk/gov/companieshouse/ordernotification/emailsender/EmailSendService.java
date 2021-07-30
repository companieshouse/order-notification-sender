package uk.gov.companieshouse.ordernotification.emailsender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.messageproducer.MessageProducer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

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
        try {
            producer.sendMessage(event.getEmailModel(), event.getOrderURL(), EMAIL_SEND_TOPIC);
        } catch (SerializationException e) {
            throw new NonRetryableFailureException("Failed to serialize email data as avro", e);
        } catch (ExecutionException | TimeoutException e) {
            loggingUtils.getLogger().error("Error sending email data to Kafka", e, loggingUtils.createLogMap());
            applicationEventPublisher.publishEvent(new EmailSendFailedEvent(event));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new NonRetryableFailureException("Interrupted", e);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
