package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class CertificateOrderNotificationModel extends OrderModel {

    private String orderReferenceNumber;
    private String companyName;
    private String companyNumber;
    private String certificateType;
    private boolean statementOfGoodStanding;
    private String registeredOfficeAddressType;
    private boolean includeRegisteredOfficeAddressDates;
    private boolean allDirectorsNames;
    private boolean allSecretaryNames;
    private boolean companyObjects;
    private String amountPaid;
    private String paymentReference;
    private String paymentTime;

    public String getOrderReferenceNumber() {
        return orderReferenceNumber;
    }

    public void setOrderReferenceNumber(String orderReferenceNumber) {
        this.orderReferenceNumber = orderReferenceNumber;
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

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public boolean isStatementOfGoodStanding() {
        return statementOfGoodStanding;
    }

    public void setStatementOfGoodStanding(boolean statementOfGoodStanding) {
        this.statementOfGoodStanding = statementOfGoodStanding;
    }

    public String getRegisteredOfficeAddressType() {
        return registeredOfficeAddressType;
    }

    public void setRegisteredOfficeAddressType(String registeredOfficeAddressType) {
        this.registeredOfficeAddressType = registeredOfficeAddressType;
    }

    public boolean isIncludeRegisteredOfficeAddressDates() {
        return includeRegisteredOfficeAddressDates;
    }

    public void setIncludeRegisteredOfficeAddressDates(boolean includeRegisteredOfficeAddressDates) {
        this.includeRegisteredOfficeAddressDates = includeRegisteredOfficeAddressDates;
    }

    public boolean isAllDirectorsNames() {
        return allDirectorsNames;
    }

    public void setAllDirectorsNames(boolean allDirectorsNames) {
        this.allDirectorsNames = allDirectorsNames;
    }

    public boolean isAllSecretaryNames() {
        return allSecretaryNames;
    }

    public void setAllSecretaryNames(boolean allSecretaryNames) {
        this.allSecretaryNames = allSecretaryNames;
    }

    public boolean isCompanyObjects() {
        return companyObjects;
    }

    public void setCompanyObjects(boolean companyObjects) {
        this.companyObjects = companyObjects;
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
        if (this == o) return true;
        if (!(o instanceof CertificateOrderNotificationModel)) return false;
        CertificateOrderNotificationModel that = (CertificateOrderNotificationModel) o;
        return isStatementOfGoodStanding() == that.isStatementOfGoodStanding() &&
                isIncludeRegisteredOfficeAddressDates() == that.isIncludeRegisteredOfficeAddressDates() &&
                isAllDirectorsNames() == that.isAllDirectorsNames() &&
                isAllSecretaryNames() == that.isAllSecretaryNames() &&
                isCompanyObjects() == that.isCompanyObjects() &&
                Objects.equals(getOrderReferenceNumber(), that.getOrderReferenceNumber()) &&
                Objects.equals(getCompanyName(), that.getCompanyName()) &&
                Objects.equals(getCompanyNumber(), that.getCompanyNumber()) &&
                Objects.equals(getCertificateType(), that.getCertificateType()) &&
                Objects.equals(getRegisteredOfficeAddressType(), that.getRegisteredOfficeAddressType()) &&
                Objects.equals(getAmountPaid(), that.getAmountPaid()) &&
                Objects.equals(getPaymentReference(), that.getPaymentReference()) &&
                Objects.equals(getPaymentTime(), that.getPaymentTime());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOrderReferenceNumber(), getCompanyName(), getCompanyNumber(), getCertificateType(), isStatementOfGoodStanding(), getRegisteredOfficeAddressType(), isIncludeRegisteredOfficeAddressDates(), isAllDirectorsNames(), isAllSecretaryNames(), isCompanyObjects(), getAmountPaid(), getPaymentReference(), getPaymentTime());
    }
}
