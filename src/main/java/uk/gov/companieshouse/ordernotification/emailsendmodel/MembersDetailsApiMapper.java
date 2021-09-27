package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseMemberDetailsApi;

import java.util.ArrayList;

@Component
@Configuration
@ConfigurationProperties(prefix = "members.details.text")
@PropertySource("classpath:application.properties")
public class MembersDetailsApiMapper {
    private String address;
    private String appointmentDate;
    private String countryOfResidence;
    private String dob;

    public CertificateDetailsModel map(BaseMemberDetailsApi baseMembersDetailsApi) {
        ListHelper helper = new ListHelper(new ArrayList<>());
        helper.add(baseMembersDetailsApi.getIncludeAddress(), getAddress());
        helper.add(baseMembersDetailsApi.getIncludeAppointmentDate(), getAppointmentDate());
        helper.add(baseMembersDetailsApi.getIncludeCountryOfResidence(), getCountryOfResidence());
        helper.add(baseMembersDetailsApi.getIncludeDobType() != null, getDob());

        return new CertificateDetailsModel(helper.size() > 0 || baseMembersDetailsApi.getIncludeBasicInformation(), helper.toList());
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

    public String getDob() {
        return dob;
    }
}
