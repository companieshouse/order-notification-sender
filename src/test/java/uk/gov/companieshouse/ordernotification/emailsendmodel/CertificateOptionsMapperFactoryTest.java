package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import uk.gov.companieshouse.ordernotification.config.ApplicationConfig;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig(ApplicationConfig.class)
public class CertificateOptionsMapperFactoryTest {

    @Autowired
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
