package uk.gov.companieshouse.ordernotification.emailsender;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class EmailSendService {

    private final EmailSendMessageProducer producer;
    private final LoggingUtils loggingUtils;

    @Autowired
    public EmailSendService(EmailSendMessageProducer producer, LoggingUtils loggingUtils) {
        this.producer = producer;
        this.loggingUtils = loggingUtils;
    }

    @EventListener
    public void handleEvent(SendEmailEvent event) throws SerializationException, ExecutionException, InterruptedException, TimeoutException {
        try {
            producer.sendMessage(event.getEmailModel(), event.getOrderReference());
        } catch (SerializationException e) {
            throw new NonRetryableFailureException("Failed to serialize email data as avro", e);
        }

    }

}
