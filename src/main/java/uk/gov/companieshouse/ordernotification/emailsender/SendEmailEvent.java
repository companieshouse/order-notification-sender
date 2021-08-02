package uk.gov.companieshouse.ordernotification.emailsender;

import uk.gov.companieshouse.ordernotification.eventmodel.OrderIdentifiable;

import java.util.Objects;

/**
 * Raised when an enriched order notification is ready to be published.
 */
public class SendEmailEvent implements OrderIdentifiable {

    private final String orderURI;
    private final int retryCount;
    private final EmailSend emailModel;

    public SendEmailEvent(String orderURI, int retryCount, EmailSend emailModel) {
        this.orderURI = orderURI;
        this.retryCount = retryCount;
        this.emailModel = emailModel;
    }

    public String getOrderURI() {
        return orderURI;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public EmailSend getEmailModel() {
        return emailModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SendEmailEvent that = (SendEmailEvent) o;
        return retryCount == that.retryCount && Objects.equals(orderURI, that.orderURI) && Objects.equals(emailModel, that.emailModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderURI, retryCount, emailModel);
    }
}
