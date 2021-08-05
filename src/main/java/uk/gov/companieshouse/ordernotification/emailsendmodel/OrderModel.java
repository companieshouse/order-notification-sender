package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class OrderModel {
    private String to;
    private String subject;
    private String companyName;
    private String companyNumber;
    private String orderReferenceNumber;
    private String amountPaid;
    private String paymentReference;
    private String paymentTime;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getOrderReferenceNumber() {
        return orderReferenceNumber;
    }

    public void setOrderReferenceNumber(String orderReferenceNumber) {
        this.orderReferenceNumber = orderReferenceNumber;
    }

    public String getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(String amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(String paymentReference) {
        this.paymentReference = paymentReference;
    }

    public String getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(String paymentTime) {
        this.paymentTime = paymentTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderModel)) {
            return false;
        }
        OrderModel that = (OrderModel) o;
        return Objects.equals(getTo(), that.getTo()) &&
                Objects.equals(getSubject(), that.getSubject()) &&
                Objects.equals(getCompanyName(), that.getCompanyName()) &&
                Objects.equals(getCompanyNumber(), that.getCompanyNumber()) &&
                Objects.equals(getOrderReferenceNumber(), that.getOrderReferenceNumber()) &&
                Objects.equals(getAmountPaid(), that.getAmountPaid()) &&
                Objects.equals(getPaymentReference(), that.getPaymentReference()) &&
                Objects.equals(getPaymentTime(), that.getPaymentTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTo(), getSubject(), getCompanyName(), getCompanyNumber(), getOrderReferenceNumber(), getAmountPaid(), getPaymentReference(), getPaymentTime());
    }
}
