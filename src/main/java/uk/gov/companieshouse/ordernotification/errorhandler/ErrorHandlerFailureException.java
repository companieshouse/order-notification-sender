package uk.gov.companieshouse.ordernotification.errorhandler;

/**
 * Raised when an error occurs handling another error
 */
public class ErrorHandlerFailureException extends RuntimeException {

    public ErrorHandlerFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
