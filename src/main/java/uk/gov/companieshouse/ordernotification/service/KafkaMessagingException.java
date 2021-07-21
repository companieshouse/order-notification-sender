package uk.gov.companieshouse.ordernotification.service;

public class KafkaMessagingException extends RuntimeException {

    public KafkaMessagingException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaMessagingException(String message) {
        super(message);
    }
}
