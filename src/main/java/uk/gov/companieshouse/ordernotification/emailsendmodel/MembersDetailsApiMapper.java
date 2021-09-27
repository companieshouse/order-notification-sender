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
    private String dobType;

    CertificateDetailsModel map(BaseMemberDetailsApi baseMembersDetailsApi) {
        ListHelper helper = new ListHelper(new ArrayList<String>());
        helper.add(baseMembersDetailsApi.getIncludeAddress(), address)
                .add(baseMembersDetailsApi.getIncludeAppointmentDate(), appointmentDate)
                .add(baseMembersDetailsApi.getIncludeCountryOfResidence(), countryOfResidence)
                .add(baseMembersDetailsApi.getIncludeDobType() != null, dobType);

        return new CertificateDetailsModel(helper.size() > 0 || baseMembersDetailsApi.getIncludeBasicInformation(), helper.getList());
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

    public void setDobType(String dobType) {
        this.dobType = dobType;
    }
}
