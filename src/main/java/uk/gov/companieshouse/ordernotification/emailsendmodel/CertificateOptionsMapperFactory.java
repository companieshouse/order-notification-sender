package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CertificateOptionsMapperFactory {

    private Map<String, CertificateOptionsMapper> certificateOptionsMappings;
    private CertificateOptionsMapper defaultMapper;

    @Autowired
    public CertificateOptionsMapperFactory(LLPCertificateOptionsMapper llpCertificateOptionsMapper,
                                           LPCertificateOptionsMapper lpCertificateOptionsMapper,
                                           OtherCertificateOptionsMapper defaultMapper) {
        certificateOptionsMappings = new HashMap<>();
        certificateOptionsMappings.put("llp", llpCertificateOptionsMapper);
        certificateOptionsMappings.put("limited-partnership", lpCertificateOptionsMapper);
        this.defaultMapper = defaultMapper;
    }

    public CertificateOptionsMapper getCertificateOptionsMapper(String companyType) {
        return certificateOptionsMappings.getOrDefault(companyType, defaultMapper);
    }
}
