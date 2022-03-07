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
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@ExtendWith(MockitoExtension.class)
class CertificateOrderNotificationMapperTest {

    private CertificateOrderNotificationMapper certificateOrderNotificationMapper;

    @Mock
    private EmailConfiguration config;

    @Mock
    private EmailDataConfiguration emailDataConfig;

    @Mock
    private CertificateOptionsMapperFactory mapperFactory;

    @Mock
    private OrdersApiDetails ordersApiDetails;

    @Mock
    private CertificateOrderNotificationModel orderModel;

    @Mock
    private CertificateOptionsMapper certificateOptionsMapper;

    @Mock
    private CertificateItemOptionsApi certificateItemOptionsApi;

    @BeforeEach
    void setup() {
        certificateOrderNotificationMapper = new CertificateOrderNotificationMapper(config,
                mapperFactory);
    }

    @Test
    void testCorrectlyMapsCertificateOrderApiMapsToOrderDetails() {
        //given
        when(ordersApiDetails.getBaseItemOptions()).thenReturn(certificateItemOptionsApi);
        when(config.getCertificate()).thenReturn(emailDataConfig);
        when(emailDataConfig.getMessageId()).thenReturn(TestConstants.MESSAGE_ID);
        when(emailDataConfig.getMessageType()).thenReturn(TestConstants.MESSAGE_TYPE);
        when(certificateItemOptionsApi.getCompanyType()).thenReturn("ltd");
        when(mapperFactory.getCertificateOptionsMapper("ltd")).thenReturn(certificateOptionsMapper);
        when(certificateOptionsMapper.generateEmailData(ordersApiDetails)).thenReturn(orderModel);

        //when
        OrderDetails orderDetails = certificateOrderNotificationMapper.map(ordersApiDetails);

        //then
        assertThat(orderDetails.getOrderModel(), is(orderModel));
        assertThat(orderDetails.getMessageId(), is(TestConstants.MESSAGE_ID));
        assertThat(orderDetails.getMessageType(), is(TestConstants.MESSAGE_TYPE));
    }
}
