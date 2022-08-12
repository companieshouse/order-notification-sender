package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class OrderNotificationEmailDataBuilderFactoryTest {

    @InjectMocks
    private OrderNotificationEmailDataBuilderFactory factory;

    @Mock
    private CertificateEmailDataMapper certificateEmailDataMapper;

    @Mock
    private CertifiedCopyEmailDataMapper certifiedCopyEmailDataMapper;

    @Mock
    private MissingImageDeliveryEmailDataMapper missingImageDeliveryEmailDataMapper;

    @Mock
    private EmailConfiguration emailConfiguration;

    @Mock
    private OrderNotificationDataConvertable converter;

    @Test
    void createNewConverterInstance() {
        // when
        OrderNotificationDataConvertable actual = factory.newConverter();

        // then
        assertEquals(new OrderNotificationEmailDataConverter(new OrderNotificationEmailData(), certificateEmailDataMapper, certifiedCopyEmailDataMapper, missingImageDeliveryEmailDataMapper, emailConfiguration), actual);
    }

    @Test
    void createNewDirectorInstance() {
        // when
        SummaryEmailDataDirector actual = factory.newDirector(converter);

        // then
        assertEquals(new SummaryEmailDataDirector(converter), actual);
    }
}
