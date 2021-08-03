package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class CertificateOrderNotificationModel extends OrderModel {

    private String certificateType;
    private boolean statementOfGoodStanding;
    private String deliveryMethod;
    private CertificateRegisteredOfficeAddressModel certificateRegisteredOfficeAddressModel;
    private CertificateAppointmentDetailsModel directorDetailsModel;
    private CertificateAppointmentDetailsModel secretaryDetailsModel;
    private boolean companyObjects;

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

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } if (o == null || getClass() != o.getClass()) {
            return false;
        } if (!super.equals(o)) {
            return false;
        }
        CertificateOrderNotificationModel that = (CertificateOrderNotificationModel) o;
        return statementOfGoodStanding == that.statementOfGoodStanding &&
                companyObjects == that.companyObjects &&
                Objects.equals(certificateType, that.certificateType) &&
                Objects.equals(deliveryMethod, that.deliveryMethod) &&
                Objects.equals(certificateRegisteredOfficeAddressModel, that.certificateRegisteredOfficeAddressModel) &&
                Objects.equals(directorDetailsModel, that.directorDetailsModel) &&
                Objects.equals(secretaryDetailsModel, that.secretaryDetailsModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), certificateType, statementOfGoodStanding, deliveryMethod, certificateRegisteredOfficeAddressModel, directorDetailsModel, secretaryDetailsModel, companyObjects);
    }
}
