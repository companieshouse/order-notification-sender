package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.List;
import java.util.Objects;

public class CertificateAppointmentDetailsModel {

    private boolean specificDetails;
    private List<String> details;

    public CertificateAppointmentDetailsModel() {
    }

    public CertificateAppointmentDetailsModel(boolean specificDetails, List<String> details) {
        this.specificDetails = specificDetails;
        this.details = details;
    }

    public boolean isSpecificDetails() {
        return specificDetails;
    }

    public void setSpecificDetails(boolean specificDetails) {
        this.specificDetails = specificDetails;
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CertificateAppointmentDetailsModel)) {
            return false;
        }
        CertificateAppointmentDetailsModel that = (CertificateAppointmentDetailsModel) o;
        return isSpecificDetails() == that.isSpecificDetails() &&
                Objects.equals(getDetails(), that.getDetails());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isSpecificDetails(), getDetails());
    }
}
