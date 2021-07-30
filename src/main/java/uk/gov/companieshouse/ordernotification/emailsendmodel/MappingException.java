package uk.gov.companieshouse.ordernotification.emailsendmodel;

/**
 * Raised if an error occurs when mapping an order resource.
 */
public class MappingException extends RuntimeException {

    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
