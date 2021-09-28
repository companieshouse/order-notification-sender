package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;

@Component
@Configuration
@ConfigurationProperties(prefix = "appointment.details.text")
@PropertySource("classpath:application.properties")
public class DirectorOrSecretaryDetailsApiMapper {
    private String address;
    private String appointmentDate;
    private String countryOfResidence;
    private String nationality;
    private String occupation;
    private String dob;

    public CertificateDetailsModel map(DirectorOrSecretaryDetailsApi appointment) {
        return new CertificateDetailsModelBuilder()
                .includeBasicInformation(appointment.getIncludeBasicInformation())
                .includeText(appointment.getIncludeAddress(), address)
                .includeText(appointment.getIncludeAppointmentDate(), appointmentDate)
                .includeText(appointment.getIncludeCountryOfResidence(), countryOfResidence)
                .includeText(appointment.getIncludeNationality(), nationality)
                .includeText(appointment.getIncludeOccupation(), occupation)
                .includeText(appointment.getIncludeDobType() != null, dob)
                .build();
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
