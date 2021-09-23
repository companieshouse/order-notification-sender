package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Map;

public class CertificateOptionsMapperFactory {

    private Map<String, CertificateOptionsMapper> certificateOptionsMappings;
    private CertificateOptionsMapper defaultMapper;

    public CertificateOptionsMapperFactory(Map<String, CertificateOptionsMapper> certificateOptionsMappings,
                                           CertificateOptionsMapper defaultMapper) {
        this.certificateOptionsMappings = certificateOptionsMappings;
        this.defaultMapper = defaultMapper;
    }

    public CertificateOptionsMapper getCertificateOptionsMapper(String companyType) {
        return certificateOptionsMappings.getOrDefault(companyType, defaultMapper);
    }
}
