package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class CertificateOrderNotificationModel extends OrderModel {

    private String certificateType;
    private String statementOfGoodStanding;
    private String deliveryMethod;
    private String registeredOfficeAddressDetails;
    private CertificateAppointmentDetailsModel directorDetailsModel;
    private CertificateAppointmentDetailsModel secretaryDetailsModel;
    private String companyObjects;

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getStatementOfGoodStanding() {
        return statementOfGoodStanding;
    }

    public void setStatementOfGoodStanding(String statementOfGoodStanding) {
        this.statementOfGoodStanding = statementOfGoodStanding;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getRegisteredOfficeAddressDetails() {
        return registeredOfficeAddressDetails;
    }

    public void setRegisteredOfficeAddressDetails(String registeredOfficeAddressDetails) {
        this.registeredOfficeAddressDetails = registeredOfficeAddressDetails;
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

    public String getCompanyObjects() {
        return companyObjects;
    }

    public void setCompanyObjects(String companyObjects) {
        this.companyObjects = companyObjects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificateOrderNotificationModel)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        CertificateOrderNotificationModel that = (CertificateOrderNotificationModel) o;
        return Objects.equals(getCertificateType(), that.getCertificateType()) &&
                Objects.equals(getStatementOfGoodStanding(), that.getStatementOfGoodStanding()) &&
                Objects.equals(getDeliveryMethod(), that.getDeliveryMethod()) &&
                Objects.equals(getRegisteredOfficeAddressDetails(), that.getRegisteredOfficeAddressDetails()) &&
                Objects.equals(getDirectorDetailsModel(), that.getDirectorDetailsModel()) &&
                Objects.equals(getSecretaryDetailsModel(), that.getSecretaryDetailsModel()) &&
                Objects.equals(getCompanyObjects(), that.getCompanyObjects());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getCertificateType(), getStatementOfGoodStanding(), getDeliveryMethod(), getRegisteredOfficeAddressDetails(), getDirectorDetailsModel(), getSecretaryDetailsModel(), getCompanyObjects());
    }
}
