package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;

import java.util.Map;

@Component("certificateTypeMapper")
public class CertificateTypeMapper {
    private Map<CertificateTypeApi, String> certificateTypeMappings;

    @Autowired
    public CertificateTypeMapper(@Qualifier("certificateTypeMappings") Map<CertificateTypeApi, String> certificateTypeMappings) {
        this.certificateTypeMappings = certificateTypeMappings;
    }

    public String mapCertificateType(CertificateTypeApi certificateType){
        return certificateTypeMappings.get(certificateType);
    }
}
