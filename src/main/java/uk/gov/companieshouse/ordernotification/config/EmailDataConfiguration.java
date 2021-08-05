package uk.gov.companieshouse.ordernotification.config;

import java.util.Objects;

public class EmailDataConfiguration {

    private String messageId;
    private String messageType;
    private String filingHistoryDateFormat;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmailDataConfiguration)) {
            return false;
        }
        EmailDataConfiguration that = (EmailDataConfiguration) o;
        return Objects.equals(getMessageId(), that.getMessageId()) &&
                Objects.equals(getMessageType(), that.getMessageType()) &&
                Objects.equals(getFilingHistoryDateFormat(), that.getFilingHistoryDateFormat());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMessageId(), getMessageType(), getFilingHistoryDateFormat());
    }
}
