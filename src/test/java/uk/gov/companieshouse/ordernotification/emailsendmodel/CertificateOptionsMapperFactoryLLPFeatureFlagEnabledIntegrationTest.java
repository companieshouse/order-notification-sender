package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.ordernotification.config.TestConfig;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Import({TestConfig.class})
@TestPropertySource(locations = {"classpath:application-stubbed.properties"})
@ActiveProfiles("llp-feature-flag-enabled")
class CertificateOptionsMapperFactoryLLPFeatureFlagEnabledIntegrationTest {

    @Autowired
    private CertificateOptionsMapperFactory mapperFactory;

    @Test
    void returnOtherCertificateOptionsMapperWhenCompanyTypeMatch() {
        // given
        // when
        CertificateOptionsMapper certificateOptionsMapper = mapperFactory.getCertificateOptionsMapper(TestConstants.LIMITED_COMPANY_TYPE);

        // then
        assertTrue(certificateOptionsMapper instanceof OtherCertificateOptionsMapper);
    }

    @Test
    void returnLLPCertificateOptionsMapperWhenCompanyTypeLLP() {
        // given
        // when
        CertificateOptionsMapper certificateOptionsMapper = mapperFactory.getCertificateOptionsMapper(CompanyType.LIMITED_LIABILITY_PARTNERSHIP);

        // then
        assertTrue(certificateOptionsMapper instanceof LLPCertificateOptionsMapper);
    }

    @Test
    void returnOtherCertificateOptionsMapperWhenCompanyTypeLP() {
        // given
        // when
        CertificateOptionsMapper certificateOptionsMapper = mapperFactory.getCertificateOptionsMapper(CompanyType.LIMITED_PARTNERSHIP);

        // then
        assertTrue(certificateOptionsMapper instanceof OtherCertificateOptionsMapper);
    }
}
