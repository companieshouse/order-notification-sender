package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateOrderNotificationMapperTest {

    private CertificateOrderNotificationMapper certificateOrderNotificationMapper;

    @Mock
    private DateGenerator dateGenerator;

    @Mock
    private EmailConfiguration config;

    @Mock
    private EmailDataConfiguration emailDataConfig;

    @Mock
    private CertificateOptionsMapperFactory certificateOptionsMapperFactory;

    @BeforeEach
    void setup() {
        certificateOrderNotificationMapper = new CertificateOrderNotificationMapper(dateGenerator,
                config, new ObjectMapper(), certificateOptionsMapperFactory);
    }

    @Test
    void testCertificateOrderNotificationMapperReturnsMessageId() {
        //given
        when(config.getCertificate()).thenReturn(emailDataConfig);
        when(emailDataConfig.getMessageId()).thenReturn(TestConstants.MESSAGE_ID);

        //when
        String actual = certificateOrderNotificationMapper.getMessageId();

        //then
        assertEquals(TestConstants.MESSAGE_ID, actual);
    }

    @Test
    void testCertificateOrderNotificationMapperReturnsMessageType() {
        //given
        when(config.getCertificate()).thenReturn(emailDataConfig);
        when(emailDataConfig.getMessageType()).thenReturn(TestConstants.MESSAGE_TYPE);

        //when
        String actual = certificateOrderNotificationMapper.getMessageType();

        //then
        assertEquals(TestConstants.MESSAGE_TYPE, actual);
    }
}
