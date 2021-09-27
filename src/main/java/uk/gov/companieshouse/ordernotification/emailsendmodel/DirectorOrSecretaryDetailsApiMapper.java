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

    CertificateDetailsModel map(DirectorOrSecretaryDetailsApi appointment) {
        ListHelper helper = new ListHelper(new ArrayList<String>());
        helper.add(appointment.getIncludeAddress(), address);
        helper.add(appointment.getIncludeAppointmentDate(), appointmentDate);
        helper.add(appointment.getIncludeCountryOfResidence(), countryOfResidence);
        helper.add(appointment.getIncludeNationality(), nationality);
        helper.add(appointment.getIncludeOccupation(), occupation);
        helper.add(appointment.getIncludeDobType() != null, dob);

        return new CertificateDetailsModel(helper.size() > 0 || appointment.getIncludeBasicInformation(), helper.getList());
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
