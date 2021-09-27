package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;

import java.util.ArrayList;

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

    public DirectorOrSecretaryDetailsApiMapper() {
    }

    public CertificateDetailsModel map(DirectorOrSecretaryDetailsApi appointment) {
        ListHelper helper = new ListHelper(new ArrayList<String>(), appointment.getIncludeBasicInformation());
        helper.add(appointment.getIncludeAddress(), getAddress());
        helper.add(appointment.getIncludeAppointmentDate(), getAppointmentDate());
        helper.add(appointment.getIncludeCountryOfResidence(), getCountryOfResidence());
        helper.add(appointment.getIncludeNationality(), getNationality());
        helper.add(appointment.getIncludeOccupation(), getOccupation());
        helper.add(appointment.getIncludeDobType() != null, getDob());

        return helper.certificateDetailsModel();
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

    public String getAddress() {
        return address;
    }

    public String getAppointmentDate() {
        return appointmentDate;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public String getNationality() {
        return nationality;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getDob() {
        return dob;
    }
}
