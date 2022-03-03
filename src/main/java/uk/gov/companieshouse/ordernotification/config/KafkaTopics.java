package uk.gov.companieshouse.ordernotification.config;

public class KafkaTopics {
    private String emailSend;
    private String orderReceived;
    private String orderReceivedRetry;
    private String orderReceivedError;

    public void setEmailSend(String emailSend) {
        this.emailSend = emailSend;
    }

    public void setOrderReceived(String orderReceived) {
        this.orderReceived = orderReceived;
    }

    public void setOrderReceivedRetry(String orderReceivedRetry) {
        this.orderReceivedRetry = orderReceivedRetry;
    }

    public void setOrderReceivedError(String orderReceivedError) {
        this.orderReceivedError = orderReceivedError;
    }

    public String getEmailSend() {
        return emailSend;
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
}
