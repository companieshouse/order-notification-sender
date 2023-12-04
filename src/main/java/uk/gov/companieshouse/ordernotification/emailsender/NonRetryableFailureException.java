package uk.gov.companieshouse.ordernotification.emailsender;

/**
 * An unrecoverable error has occurred when handling a message.
 */
public class NonRetryableFailureException extends RuntimeException {

    public NonRetryableFailureException(String message) {
        super(message);
    }

    public NonRetryableFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
