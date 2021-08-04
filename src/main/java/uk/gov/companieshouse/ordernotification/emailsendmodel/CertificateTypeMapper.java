package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;

import java.util.Map;

/**
 * Maps {@link CertificateTypeApi} objects to human readable strings.
 */
@Component
public class CertificateTypeMapper {
    private Map<CertificateTypeApi, String> certificateTypeMappings;

    @Autowired
    public CertificateTypeMapper(@Qualifier("certificateTypeMappings") Map<CertificateTypeApi, String> certificateTypeMappings) {
        this.certificateTypeMappings = certificateTypeMappings;
    }

    /**
     * Maps {@link CertificateTypeApi} objects to human readable strings.
     *
     * @param certificateType An enum of certificate types
     * @return A string representation of the type
     */
    public String mapCertificateType(CertificateTypeApi certificateType){
        return certificateTypeMappings.get(certificateType);
    }
}
