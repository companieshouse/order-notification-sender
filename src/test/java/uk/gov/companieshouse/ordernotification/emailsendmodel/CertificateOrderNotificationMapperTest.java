package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    private CertificateOptionsMapperFactory mapperFactory;

    @Mock
    private OrdersApiDetails ordersApiDetails;

    @Mock
    private CertificateOrderNotificationModel orderModel;

    @Mock
    private CertificateOptionsMapper certificateOptionsMapper;

    @Mock
    private CertificateItemOptionsApi certificateItemOptionsApi;

    @Mock
    private CertificateOrderMessageTypeFactory messageTypeFactory;

    @Mock
    private EmailDataConfiguration messageConfiguration;

    @BeforeEach
    void setup() {
        certificateOrderNotificationMapper = new CertificateOrderNotificationMapper(mapperFactory, messageTypeFactory);
    }

    @Test
    @DisplayName("Test certificate order notification mapper correctly maps orders API details to order details model")
    void testCorrectlyMapsCertificateOrderApiToOrderDetails() {
        //given
        when(ordersApiDetails.getItemOptions()).thenReturn(certificateItemOptionsApi);
        when(certificateItemOptionsApi.getCompanyType()).thenReturn("ltd");
        when(mapperFactory.getCertificateOptionsMapper("ltd")).thenReturn(certificateOptionsMapper);
        when(certificateOptionsMapper.generateEmailData(ordersApiDetails)).thenReturn(orderModel);
        when(messageTypeFactory.getMessageConfiguration(any())).thenReturn(messageConfiguration);
        when(messageConfiguration.getMessageId()).thenReturn(TestConstants.MESSAGE_ID);
        when(messageConfiguration.getMessageType()).thenReturn(TestConstants.MESSAGE_TYPE);

        //when
        OrderDetails orderDetails = certificateOrderNotificationMapper.map(ordersApiDetails);

        //then
        assertThat(orderDetails.getOrderModel(), is(orderModel));
        assertThat(orderDetails.getMessageId(), is(TestConstants.MESSAGE_ID));
        assertThat(orderDetails.getMessageType(), is(TestConstants.MESSAGE_TYPE));
    }
}
