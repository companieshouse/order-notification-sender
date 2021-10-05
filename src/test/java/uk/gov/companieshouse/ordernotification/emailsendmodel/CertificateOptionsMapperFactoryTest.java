package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.ordernotification.config.FeatureOptions;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateOptionsMapperFactoryTest {

    @Mock
    private LLPCertificateOptionsMapper llpCertificateOptionsMapper;
    @Mock
    private LPCertificateOptionsMapper lpCertificateOptionsMapper;
    @Mock
    private OtherCertificateOptionsMapper otherCertificateOptionsMapper;
    @Mock
    private FeatureOptions featureOptions;

    @BeforeEach
    void setUp() {
    }

    @Test
    void returnOtherCertificateOptionsMapperWhenCompanyTypeMatch() {
        // given
        when(featureOptions.isLpCertificateOrdersEnabled()).thenReturn(true);
        when(featureOptions.isLlpCertificateOrdersEnabled()).thenReturn(true);

        // when
        CertificateOptionsMapperFactory mapperFactory = new CertificateOptionsMapperFactory(llpCertificateOptionsMapper, lpCertificateOptionsMapper, otherCertificateOptionsMapper, featureOptions);
        CertificateOptionsMapper certificateOptionsMapper = mapperFactory.getCertificateOptionsMapper(TestConstants.LIMITED_COMPANY_TYPE);

        // then
        assertTrue(certificateOptionsMapper instanceof OtherCertificateOptionsMapper);
    }

    @Test
    void returnLLPCertificateOptionsMapperWhenCompanyTypeLLP() {
        // given
        when(featureOptions.isLpCertificateOrdersEnabled()).thenReturn(false);
        when(featureOptions.isLlpCertificateOrdersEnabled()).thenReturn(true);

        // when
        CertificateOptionsMapperFactory mapperFactory = new CertificateOptionsMapperFactory(llpCertificateOptionsMapper, lpCertificateOptionsMapper, otherCertificateOptionsMapper, featureOptions);
        CertificateOptionsMapper certificateOptionsMapper = mapperFactory.getCertificateOptionsMapper(CompanyType.LIMITED_LIABILITY_PARTNERSHIP);

        // then
        assertTrue(certificateOptionsMapper instanceof LLPCertificateOptionsMapper);
    }

    @Test
    void returnLLPCertificateOptionsMapperWhenCompanyTypeLP() {
        // given
        when(featureOptions.isLpCertificateOrdersEnabled()).thenReturn(true);
        when(featureOptions.isLlpCertificateOrdersEnabled()).thenReturn(false);

        // when
        CertificateOptionsMapperFactory mapperFactory = new CertificateOptionsMapperFactory(llpCertificateOptionsMapper, lpCertificateOptionsMapper, otherCertificateOptionsMapper, featureOptions);
        CertificateOptionsMapper certificateOptionsMapper = mapperFactory.getCertificateOptionsMapper(CompanyType.LIMITED_PARTNERSHIP);

        // then
        assertTrue(certificateOptionsMapper instanceof LPCertificateOptionsMapper);
    }
}
