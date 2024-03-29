package uk.gov.companieshouse.ordernotification.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "email")
@Component
public class EmailConfiguration implements MessageTypeConfigProvider {
    private String dateFormat;
    private String senderAddress;
    private String paymentDateFormat;
    private String applicationId;
    private String confirmationMessage;
    private int dispatchDays;
    private String messageId;
    private String messageType;
    private String filingHistoryDateFormat;
    private String chsUrl;

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getPaymentDateFormat() {
        return paymentDateFormat;
    }

    public void setPaymentDateFormat(String paymentDateFormat) {
        this.paymentDateFormat = paymentDateFormat;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getConfirmationMessage() {
        return confirmationMessage;
    }

    public void setConfirmationMessage(String confirmationMessage) {
        this.confirmationMessage = confirmationMessage;
    }

    public int getDispatchDays() {
        return dispatchDays;
    }

    public void setDispatchDays(int dispatchDays) {
        this.dispatchDays = dispatchDays;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getFilingHistoryDateFormat() {
        return filingHistoryDateFormat;
    }

    public void setFilingHistoryDateFormat(String filingHistoryDateFormat) {
        this.filingHistoryDateFormat = filingHistoryDateFormat;
    }

    public String getChsUrl() {
        return chsUrl;
    }

    public void setChsUrl(String chsUrl) {
        this.chsUrl = chsUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EmailConfiguration that = (EmailConfiguration) o;
        return dispatchDays == that.dispatchDays && Objects.equals(dateFormat, that.dateFormat) && Objects.equals(senderAddress, that.senderAddress) && Objects.equals(paymentDateFormat, that.paymentDateFormat) && Objects.equals(applicationId, that.applicationId) && Objects.equals(confirmationMessage, that.confirmationMessage) && Objects.equals(messageId, that.messageId) && Objects.equals(messageType, that.messageType) && Objects.equals(filingHistoryDateFormat, that.filingHistoryDateFormat) && Objects.equals(chsUrl, that.chsUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateFormat, senderAddress, paymentDateFormat, applicationId, confirmationMessage, dispatchDays, messageId, messageType, filingHistoryDateFormat, chsUrl);
    }
}
