package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

@ExtendWith(MockitoExtension.class)
class CertificateOrderNotificationMapperTest {

    private CertificateOrderNotificationMapper certificateOrderNotificationMapper;

    @Mock
    private EmailConfiguration config;

    @Mock
    private EmailDataConfiguration emailDataConfig;

    @Mock
    private CertificateOrderModelFactory orderModelFactory;

    @Mock
    private OrdersApiDetails ordersApiDetails;

    @Mock
    private CertificateOrderNotificationModel orderModel;

    @BeforeEach
    void setup() {
        certificateOrderNotificationMapper = new CertificateOrderNotificationMapper(config,
                orderModelFactory);
    }

    @Test
    void testCorrectlyMapsCertificateOrderApiMapsToOrderDetails() {
        //given
        when(config.getCertificate()).thenReturn(emailDataConfig);
        when(emailDataConfig.getMessageId()).thenReturn(TestConstants.MESSAGE_ID);
        when(emailDataConfig.getMessageType()).thenReturn(TestConstants.MESSAGE_TYPE);
        when(orderModelFactory.newInstance(any())).thenReturn(orderModel);

        //when
        OrderDetails orderDetails = certificateOrderNotificationMapper.map(ordersApiDetails);

        //then
        assertThat(orderDetails.getOrderModel(), is(orderModel));
        assertThat(orderDetails.getMessageId(), is(TestConstants.MESSAGE_ID));
        assertThat(orderDetails.getMessageType(), is(TestConstants.MESSAGE_TYPE));
    }
}
