package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;

import java.util.Arrays;
import java.util.List;

@Component
@Configuration
@ConfigurationProperties(prefix = "appointment.details.text")
@PropertySource("classpath:application.properties")
public class AppointmentDetailsDictionary {

    private String address;
    private String appointmentDate;
    private String countryOfResidence;
    private String nationality;
    private String occupation;
    private String dob;

    public List<Boolean> evaluateAppointment(DirectorOrSecretaryDetailsApi appointment) {
        return Arrays.asList(
                appointment.getIncludeAddress(),
                appointment.getIncludeAppointmentDate(),
                appointment.getIncludeCountryOfResidence(),
                appointment.getIncludeNationality(),
                appointment.getIncludeOccupation(),
                appointment.getIncludeDobType() != null);
    }

    public List<String> getText() {
        return Arrays.asList(
                address,
                appointmentDate,
                countryOfResidence,
                nationality,
                occupation,
                dob
        );
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
