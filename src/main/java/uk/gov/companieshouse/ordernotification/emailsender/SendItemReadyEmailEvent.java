package uk.gov.companieshouse.ordernotification.emailsender;

/**
 * Raised when an enriched item ready notification is ready to be published.
 */
public class SendItemReadyEmailEvent /* TODO DCAC-295 Does this help? *implements OrderIdentifiable*/ {

    private final String orderURI;
    private final EmailSend emailModel;

    public SendItemReadyEmailEvent(String orderURI, EmailSend emailModel) {
        this.orderURI = orderURI;
        this.emailModel = emailModel;
    }

    public String getOrderURI() {
        return orderURI;
    }

    public EmailSend getEmailModel() {
        return emailModel;
    }

}
