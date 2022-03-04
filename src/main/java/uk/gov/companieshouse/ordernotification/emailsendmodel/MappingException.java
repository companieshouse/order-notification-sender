package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.ordernotification.emailsender.NonRetryableFailureException;

/**
 * Raised if an error occurs when mapping an order resource.
 */
public class MappingException extends NonRetryableFailureException {

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
