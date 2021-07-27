package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class CertificateOrderNotificationModel extends OrderModel {

    private String orderReferenceNumber;
    private String companyName;
    private String companyNumber;
    private String certificateType;
    private boolean statementOfGoodStanding;
    private CertificateRegisteredOfficeAddressModel certificateRegisteredOfficeAddressModel;
    private CertificateAppointmentDetailsModel directorDetailsModel;
    private CertificateAppointmentDetailsModel secretaryDetailsModel;
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

    public CertificateRegisteredOfficeAddressModel getCertificateRegisteredOfficeAddressModel() {
        return certificateRegisteredOfficeAddressModel;
    }

    public void setCertificateRegisteredOfficeAddressModel(CertificateRegisteredOfficeAddressModel certificateRegisteredOfficeAddressModel) {
        this.certificateRegisteredOfficeAddressModel = certificateRegisteredOfficeAddressModel;
    }

    public CertificateAppointmentDetailsModel getDirectorDetailsModel() {
        return directorDetailsModel;
    }

    public void setDirectorDetailsModel(CertificateAppointmentDetailsModel directorDetailsModel) {
        this.directorDetailsModel = directorDetailsModel;
    }

    public CertificateAppointmentDetailsModel getSecretaryDetailsModel() {
        return secretaryDetailsModel;
    }

    public void setSecretaryDetailsModel(CertificateAppointmentDetailsModel secretaryDetailsModel) {
        this.secretaryDetailsModel = secretaryDetailsModel;
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
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateOrderNotificationModel that = (CertificateOrderNotificationModel) o;
        return statementOfGoodStanding == that.statementOfGoodStanding &&
                companyObjects == that.companyObjects &&
                Objects.equals(orderReferenceNumber, that.orderReferenceNumber) &&
                Objects.equals(companyName, that.companyName) &&
                Objects.equals(companyNumber, that.companyNumber) &&
                Objects.equals(certificateType, that.certificateType) &&
                Objects.equals(certificateRegisteredOfficeAddressModel, that.certificateRegisteredOfficeAddressModel) &&
                Objects.equals(directorDetailsModel, that.directorDetailsModel) &&
                Objects.equals(secretaryDetailsModel, that.secretaryDetailsModel) &&
                Objects.equals(amountPaid, that.amountPaid) &&
                Objects.equals(paymentReference, that.paymentReference) &&
                Objects.equals(paymentTime, that.paymentTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderReferenceNumber, companyName, companyNumber, certificateType, statementOfGoodStanding, certificateRegisteredOfficeAddressModel, directorDetailsModel, secretaryDetailsModel, companyObjects, amountPaid, paymentReference, paymentTime);
    }

    @Override
    public String toString() {
        return "CertificateOrderNotificationModel{" +
                "orderReferenceNumber='" + orderReferenceNumber + '\'' +
                ", companyName='" + companyName + '\'' +
                ", companyNumber='" + companyNumber + '\'' +
                ", certificateType='" + certificateType + '\'' +
                ", statementOfGoodStanding=" + statementOfGoodStanding +
                ", certificateRegisteredOfficeAddressModel=" + certificateRegisteredOfficeAddressModel +
                ", directorDetailsModel=" + directorDetailsModel +
                ", secretaryDetailsModel=" + secretaryDetailsModel +
                ", companyObjects=" + companyObjects +
                ", amountPaid='" + amountPaid + '\'' +
                ", paymentReference='" + paymentReference + '\'' +
                ", paymentTime='" + paymentTime + '\'' +
                '}';
    }
}
