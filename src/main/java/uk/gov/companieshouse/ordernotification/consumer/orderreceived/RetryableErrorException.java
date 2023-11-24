package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

/**
 * Thrown to indicate a recoverable error in processing that can be tried again. An example of a recoverable error is a
 * network connectivity error while accessing an external api that may go away during subsequent retries.
 */
public class RetryableErrorException extends RuntimeException {

    public RetryableErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
