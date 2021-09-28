package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseMemberDetailsApi;

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
        return new CertificateDetailsModelBuilder()
                .includeBasicInformation(baseMembersDetailsApi.getIncludeBasicInformation())
                .includeText(baseMembersDetailsApi.getIncludeAddress(), address)
                .includeText(baseMembersDetailsApi.getIncludeAppointmentDate(), appointmentDate)
                .includeText(baseMembersDetailsApi.getIncludeCountryOfResidence(), countryOfResidence)
                .includeText(baseMembersDetailsApi.getIncludeDobType() != null, dob)
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

    public void setDob(String dob) {
        this.dob = dob;
    }
}
