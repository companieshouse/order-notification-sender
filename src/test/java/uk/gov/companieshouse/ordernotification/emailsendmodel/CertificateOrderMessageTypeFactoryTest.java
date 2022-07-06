package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CertificateOrderMessageTypeFactoryTest {

    @InjectMocks
    private CertificateOrderMessageTypeFactory messageTypeFactory;

    @Mock
    private EmailConfiguration emailConfiguration;

    @Mock
    private EmailDataConfiguration certificateConfig;

    @Mock
    private EmailDataConfiguration dissolvedCertificateConfig;

    @Mock
    private CertificateItemOptionsApi itemOptionsApi;

    @Test
    void testGetMessageConfigurationForNonDissolutionCertOrder() {
        // given
        when(itemOptionsApi.getCertificateType()).thenReturn(CertificateTypeApi.INCORPORATION_WITH_ALL_NAME_CHANGES);
        when(emailConfiguration.getCertificate()).thenReturn(certificateConfig);

        // when
        EmailDataConfiguration actual = messageTypeFactory.getMessageConfiguration(itemOptionsApi);

        // then
        assertEquals(certificateConfig, actual);
    }

    @Test
    void testGetMessageConfigurationForDissolutionCertOrder() {
        // given
        when(itemOptionsApi.getCertificateType()).thenReturn(CertificateTypeApi.DISSOLUTION);
        when(emailConfiguration.getDissolvedCertificate()).thenReturn(dissolvedCertificateConfig);

        // when
        EmailDataConfiguration actual = messageTypeFactory.getMessageConfiguration(itemOptionsApi);

        // then
        assertEquals(dissolvedCertificateConfig, actual);
    }
}
