package uk.gov.companieshouse.ordernotification.emailsender;

/**
 * Raised when an enriched item ready notification is ready to be published.
 */
public class SendItemReadyEmailEvent {

    private final String orderURI;
    private final String itemId;
    private final EmailSend emailModel;

    public SendItemReadyEmailEvent(String orderURI, String itemId, EmailSend emailModel) {
        this.orderURI = orderURI;
        this.itemId = itemId;
        this.emailModel = emailModel;
    }

    public String getOrderURI() {
        return orderURI;
    }

    public EmailSend getEmailModel() {
        return emailModel;
    }

    public String getItemId() {
        return itemId;
    }
}
