package uk.gov.companieshouse.ordernotification.emailsender;

/**
 * An unrecoverable error has occured when handling a message.
 */
public class NonRetryableFailureException extends RuntimeException {

    public NonRetryableFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
