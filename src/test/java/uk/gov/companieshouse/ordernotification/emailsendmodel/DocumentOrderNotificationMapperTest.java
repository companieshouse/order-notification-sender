package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@ExtendWith(MockitoExtension.class)
class DocumentOrderNotificationMapperTest {

    @Mock
    private EmailConfiguration config;

    @Mock
    private EmailDataConfiguration emailDataConfig;

    @Mock
    private DocumentOrderDetailsMapper orderModelFactory;

    @Mock
    private DocumentOrderNotificationModel orderModel;

    @Mock
    private OrdersApiDetails ordersApiDetails;

    @InjectMocks
    private DocumentOrderNotificationMapper documentOrderNotificationMapper;

    @Test
    void testMapCertifiedDocument() {
        // given
        when(orderModelFactory.map(any())).thenReturn(orderModel);
        when(config.getDocument()).thenReturn(emailDataConfig);
        when(emailDataConfig.getMessageId()).thenReturn(TestConstants.MESSAGE_ID);
        when(emailDataConfig.getMessageType()).thenReturn(TestConstants.MESSAGE_TYPE);

        // when
        OrderDetails result = documentOrderNotificationMapper.map(ordersApiDetails);

        // then
        assertThat(result.getOrderModel(), is(orderModel));
        assertThat(result.getMessageId(), is(TestConstants.MESSAGE_ID));
        assertThat(result.getMessageType(), is(TestConstants.MESSAGE_TYPE));
    }
}
