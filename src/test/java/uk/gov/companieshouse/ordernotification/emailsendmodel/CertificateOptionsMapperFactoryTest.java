package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CertificateOptionsMapperFactoryTest {

    @Mock
    private LLPCertificateOptionsMapper llpCertificateOptionsMapper;
    @Mock
    private LPCertificateOptionsMapper lpCertificateOptionsMapper;
    @Mock
    private OtherCertificateOptionsMapper otherCertificateOptionsMapper;

    @InjectMocks
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
    void returnLLPCertificateOptionsMapperWhenCompanyTypeLP() {
        // given
        // when
        CertificateOptionsMapper certificateOptionsMapper = mapperFactory.getCertificateOptionsMapper(CompanyType.LIMITED_PARTNERSHIP);

        // then
        assertTrue(certificateOptionsMapper instanceof LPCertificateOptionsMapper);
    }
}
