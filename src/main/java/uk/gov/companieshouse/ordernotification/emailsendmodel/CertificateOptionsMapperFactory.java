package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.ordernotification.config.FeatureOptions;

import java.util.EnumMap;
import java.util.Map;

@Component
public class CertificateOptionsMapperFactory {

    private final Map<CompanyType, CertificateOptionsMapper> certificateOptionsMappings;
    private final CertificateOptionsMapper defaultMapper;

    @Autowired
    public CertificateOptionsMapperFactory(LLPCertificateOptionsMapper llpCertificateOptionsMapper,
                                           LPCertificateOptionsMapper lpCertificateOptionsMapper,
                                           OtherCertificateOptionsMapper defaultMapper,
                                           FeatureOptions featureOptions) {
        certificateOptionsMappings = new EnumMap<>(CompanyType.class);
        if (featureOptions.isLlpCertificateOrdersEnabled()) {
            certificateOptionsMappings.put(CompanyType.LIMITED_LIABILITY_PARTNERSHIP, llpCertificateOptionsMapper);
        }
        if (featureOptions.isLpCertificateOrdersEnabled()) {
            certificateOptionsMappings.put(CompanyType.LIMITED_PARTNERSHIP, lpCertificateOptionsMapper);
        }
        this.defaultMapper = defaultMapper;
    }

    public CertificateOptionsMapper getCertificateOptionsMapper(String companyType) {
        return getCertificateOptionsMapper(CompanyType.getEnumValue(companyType));
    }

    public CertificateOptionsMapper getCertificateOptionsMapper(CompanyType companyType) {
        return certificateOptionsMappings.getOrDefault(companyType, defaultMapper);
    }
}
