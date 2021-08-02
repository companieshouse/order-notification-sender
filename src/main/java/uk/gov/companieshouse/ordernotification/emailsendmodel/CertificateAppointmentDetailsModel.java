package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Objects;

public class CertificateAppointmentDetailsModel {

    private boolean includeAddress;
    private boolean includeAppointmentDate;
    private boolean includeBasicInformation;
    private boolean includeCountryOfResidence;
    private String includeDobType;
    private boolean includeNationality;
    private boolean includeOccupation;

    public boolean isIncludeAddress() {
        return includeAddress;
    }

    public void setIncludeAddress(boolean includeAddress) {
        this.includeAddress = includeAddress;
    }

    public boolean isIncludeAppointmentDate() {
        return includeAppointmentDate;
    }

    public void setIncludeAppointmentDate(boolean includeAppointmentDate) {
        this.includeAppointmentDate = includeAppointmentDate;
    }

    public boolean isIncludeBasicInformation() {
        return includeBasicInformation;
    }

    public void setIncludeBasicInformation(boolean includeBasicInformation) {
        this.includeBasicInformation = includeBasicInformation;
    }

    public boolean isIncludeCountryOfResidence() {
        return includeCountryOfResidence;
    }

    public void setIncludeCountryOfResidence(boolean includeCountryOfResidence) {
        this.includeCountryOfResidence = includeCountryOfResidence;
    }

    public String getIncludeDobType() {
        return includeDobType;
    }

    public void setIncludeDobType(String includeDobType) {
        this.includeDobType = includeDobType;
    }

    public boolean isIncludeNationality() {
        return includeNationality;
    }

    public void setIncludeNationality(boolean includeNationality) {
        this.includeNationality = includeNationality;
    }

    public boolean isIncludeOccupation() {
        return includeOccupation;
    }

    public void setIncludeOccupation(boolean includeOccupation) {
        this.includeOccupation = includeOccupation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateAppointmentDetailsModel that = (CertificateAppointmentDetailsModel) o;
        return includeAddress == that.includeAddress &&
                includeAppointmentDate == that.includeAppointmentDate &&
                includeBasicInformation == that.includeBasicInformation &&
                includeCountryOfResidence == that.includeCountryOfResidence &&
                includeNationality == that.includeNationality &&
                includeOccupation == that.includeOccupation &&
                Objects.equals(includeDobType, that.includeDobType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(includeAddress, includeAppointmentDate, includeBasicInformation, includeCountryOfResidence, includeDobType, includeNationality, includeOccupation);
    }
}
