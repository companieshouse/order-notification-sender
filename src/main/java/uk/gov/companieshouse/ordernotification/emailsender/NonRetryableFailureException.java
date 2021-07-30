package uk.gov.companieshouse.ordernotification.emailsender;

public class NonRetryableFailureException extends RuntimeException{

    public NonRetryableFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
