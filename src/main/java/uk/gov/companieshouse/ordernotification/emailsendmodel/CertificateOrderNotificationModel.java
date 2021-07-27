package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class CertificateOrderNotificationModel extends OrderModel {

    private String companyName;
    private String companyNumber;
    private String certificateType;
    private boolean statementOfGoodStanding;
    private CertificateRegisteredOfficeAddressModel certificateRegisteredOfficeAddressModel;
    private CertificateAppointmentDetailsModel directorDetailsModel;
    private CertificateAppointmentDetailsModel secretaryDetailsModel;
    private boolean companyObjects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificateOrderNotificationModel)) {
            return false;
        }
        CertificateOrderNotificationModel that = (CertificateOrderNotificationModel) o;
        return isStatementOfGoodStanding() == that.isStatementOfGoodStanding() &&
                isCompanyObjects() == that.isCompanyObjects() &&
                Objects.equals(getCompanyName(), that.getCompanyName()) &&
                Objects.equals(getCompanyNumber(), that.getCompanyNumber()) &&
                Objects.equals(getCertificateType(), that.getCertificateType()) &&
                Objects.equals(getCertificateRegisteredOfficeAddressModel(), that.getCertificateRegisteredOfficeAddressModel()) &&
                Objects.equals(getDirectorDetailsModel(), that.getDirectorDetailsModel()) &&
                Objects.equals(getSecretaryDetailsModel(), that.getSecretaryDetailsModel());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCompanyName(), getCompanyNumber(), getCertificateType(), isStatementOfGoodStanding(), getCertificateRegisteredOfficeAddressModel(), getDirectorDetailsModel(), getSecretaryDetailsModel(), isCompanyObjects());
    }
}
