package uk.gov.companieshouse.ordernotification.emailsender;

import org.sonarsource.scanner.api.internal.shaded.okio.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class EmailSendService implements ApplicationEventPublisherAware {

    private final EmailSendMessageProducer producer;
    private final LoggingUtils loggingUtils;
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public EmailSendService(EmailSendMessageProducer producer, LoggingUtils loggingUtils) {
        this.producer = producer;
        this.loggingUtils = loggingUtils;
    }

    @EventListener
    public void handleEvent(SendEmailEvent event) {
        try {
            producer.sendMessage(event.getEmailModel(), event.getOrderReference());
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
