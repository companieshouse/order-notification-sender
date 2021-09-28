package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CertificateOptionsMapperFactory {

    private final Map<String, CertificateOptionsMapper> certificateOptionsMappings;
    private final CertificateOptionsMapper defaultMapper;

    @Autowired
    public CertificateOptionsMapperFactory(LLPCertificateOptionsMapper llpCertificateOptionsMapper,
                                           LPCertificateOptionsMapper lpCertificateOptionsMapper,
                                           OtherCertificateOptionsMapper defaultMapper) {
        certificateOptionsMappings = new HashMap<>();
        certificateOptionsMappings.put(CompanyType.LIMITED_LIABILITY_PARTNERSHIP, llpCertificateOptionsMapper);
        certificateOptionsMappings.put(CompanyType.LIMITED_PARTNERSHIP, lpCertificateOptionsMapper);
        this.defaultMapper = defaultMapper;
    }

    public CertificateOptionsMapper getCertificateOptionsMapper(String companyType) {
        return certificateOptionsMappings.getOrDefault(companyType, defaultMapper);
    }
}
