package uk.gov.companieshouse.ordernotification.emailsender;

import uk.gov.companieshouse.ordernotification.eventmodel.OrderIdentifiable;

import java.util.Objects;

public class SendEmailEvent implements OrderIdentifiable  {
    private final String orderReference;
    private final int retryCount;
    private final EmailSend emailModel;

    public SendEmailEvent(String orderReference, int retryCount, EmailSend emailModel) {
        this.orderReference = orderReference;
        this.retryCount = retryCount;
        this.emailModel = emailModel;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public EmailSend getEmailModel() {
        return emailModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SendEmailEvent)) {
            return false;
        }
        SendEmailEvent that = (SendEmailEvent) o;
        return getRetryCount() == that.getRetryCount() &&
                Objects.equals(getOrderReference(), that.getOrderReference()) &&
                Objects.equals(getEmailModel(), that.getEmailModel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderReference(), getRetryCount(), getEmailModel());
    }
}
