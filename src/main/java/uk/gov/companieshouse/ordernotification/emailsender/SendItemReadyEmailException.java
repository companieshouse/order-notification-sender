package uk.gov.companieshouse.ordernotification.emailsender;

import uk.gov.companieshouse.ordernotification.consumer.orderreceived.RetryableErrorException;

/**
 * Raised if a retryable error occurs when sending an item ready <code>email-send</code> message.
 */
public class SendItemReadyEmailException extends RetryableErrorException {
    public SendItemReadyEmailException(String message, Throwable e) {
        super(message, e);
    }
}
