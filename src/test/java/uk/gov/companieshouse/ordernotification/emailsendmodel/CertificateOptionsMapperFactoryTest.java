package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class CertificateOptionsMapperFactoryTest {

    @Mock
    private LLPCertificateOptionsMapper llpCertificateOptionsMapper;
    @Mock
    LPCertificateOptionsMapper lpCertificateOptionsMapper;
    @Mock
    OtherCertificateOptionsMapper otherCertificateOptionsMapper;

    @InjectMocks
    private CertificateOptionsMapperFactory mapperFactory;

    @Test
    void returnOtherCertificateOptionsMapperWhenCompanyTypeMatch() {
        // given
        // when
        CertificateOptionsMapper certificateOptionsMapper = mapperFactory.getCertificateOptionsMapper("ltd");

        // then
        assertTrue(certificateOptionsMapper instanceof OtherCertificateOptionsMapper);
    }

    @Test
    void returnLLPCertificateOptionsMapperWhenCompanyTypeLLP() {
        // given
        // when
        CertificateOptionsMapper certificateOptionsMapper = mapperFactory.getCertificateOptionsMapper("llp");

        // then
        assertTrue(certificateOptionsMapper instanceof LLPCertificateOptionsMapper);
    }
}
