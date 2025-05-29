package uk.gov.companieshouse.ordernotification.config;

public class KafkaTopics {

    private String orderReceived;
    private String orderReceivedRetry;
    private String orderReceivedError;
    private String itemGroupProcessedSend;

    public void setOrderReceived(String orderReceived) {
        this.orderReceived = orderReceived;
    }

    public void setOrderReceivedRetry(String orderReceivedRetry) {
        this.orderReceivedRetry = orderReceivedRetry;
    }

    public void setOrderReceivedError(String orderReceivedError) {
        this.orderReceivedError = orderReceivedError;
    }

    public void setItemGroupProcessedSend(String itemGroupProcessedSend) {
        this.itemGroupProcessedSend = itemGroupProcessedSend;
    }

    public String getOrderReceived() {
        return orderReceived;
    }

    public String getOrderReceivedRetry() {
        return orderReceivedRetry;
    }

    public String getOrderReceivedError() {
        return orderReceivedError;
    }

    public String getItemGroupProcessedSend() {
        return itemGroupProcessedSend;
    }
}
