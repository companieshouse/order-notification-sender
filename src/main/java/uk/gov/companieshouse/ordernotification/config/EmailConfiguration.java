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
public class EmailConfiguration {
    private String dateFormat;
    private String senderAddress;
    private String paymentDateFormat;
    private String applicationId;
    private String confirmationMessage;
    private String dispatchDays;
    private EmailDataConfiguration certificate;
    private EmailDataConfiguration dissolvedCertificate;
    private EmailDataConfiguration document;
    private EmailDataConfiguration missingImage;

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

    public String getDispatchDays() {
        return dispatchDays;
    }

    public void setDispatchDays(String dispatchDays) {
        this.dispatchDays = dispatchDays;
    }

    public EmailDataConfiguration getCertificate() {
        return certificate;
    }

    public void setCertificate(EmailDataConfiguration certificate) {
        this.certificate = certificate;
    }

    public EmailDataConfiguration getDissolvedCertificate() {
        return dissolvedCertificate;
    }

    public void setDissolvedCertificate(EmailDataConfiguration dissolvedCertificate) {
        this.dissolvedCertificate = dissolvedCertificate;
    }

    public EmailDataConfiguration getDocument() {
        return document;
    }

    public void setDocument(EmailDataConfiguration document) {
        this.document = document;
    }

    public EmailDataConfiguration getMissingImage() {
        return missingImage;
    }

    public void setMissingImage(EmailDataConfiguration missingImage) {
        this.missingImage = missingImage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EmailConfiguration)) {
            return false;
        }
        EmailConfiguration that = (EmailConfiguration) o;
        return Objects.equals(getDateFormat(), that.getDateFormat()) &&
                Objects.equals(getSenderAddress(), that.getSenderAddress()) &&
                Objects.equals(getPaymentDateFormat(), that.getPaymentDateFormat()) &&
                Objects.equals(getApplicationId(), that.getApplicationId()) &&
                Objects.equals(getConfirmationMessage(), that.getConfirmationMessage()) &&
                Objects.equals(getDispatchDays(), that.getDispatchDays()) &&
                Objects.equals(getCertificate(), that.getCertificate()) &&
                Objects.equals(getDocument(), that.getDocument()) &&
                Objects.equals(getMissingImage(), that.getMissingImage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDateFormat(), getSenderAddress(), getPaymentDateFormat(), getApplicationId(), getConfirmationMessage(), getDispatchDays(), getCertificate(), getDocument(), getMissingImage());
    }
}
