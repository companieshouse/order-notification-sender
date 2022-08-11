package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.DeliveryDetailsApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

@ExtendWith(MockitoExtension.class)
class OrderNotificationEmailDataConverterTest {

    @InjectMocks
    private OrderNotificationDataConvertable converter;

    @Mock
    private OrdersApi ordersApi;

    @Mock
    private OrderNotificationEmailData emailData;

    @Mock
    private CertificateEmailDataMapper certificateEmailDataMapper;

    @Mock
    private CertifiedCopyEmailDataMapper certifiedCopyEmailDataMapper;

    @Mock
    private MissingImageDeliveryEmailDataMapper missingImageDeliveryEmailDataMapper;

    @Mock
    private EmailConfiguration emailConfiguration;

    @Mock
    private DeliveryDetailsApi deliveryDetails;

    @Test
    void testMapOrder() {
        // given
        when(ordersApi.getReference()).thenReturn(TestConstants.ORDER_REFERENCE_NUMBER);
        when(ordersApi.getDeliveryDetails()).thenReturn(deliveryDetails);
        when(deliveryDetails.getForename()).thenReturn(TestConstants.FORENAME);
        when(deliveryDetails.getSurname()).thenReturn(TestConstants.SURNAME);
        when(deliveryDetails.getAddressLine1()).thenReturn(TestConstants.ADDRESS_LINE_1);
        when(deliveryDetails.getAddressLine2()).thenReturn(TestConstants.ADDRESS_LINE_2);
        when(deliveryDetails.getLocality()).thenReturn(TestConstants.LOCALITY);
        when(deliveryDetails.getPoBox()).thenReturn(TestConstants.PO_BOX);
        when(deliveryDetails.getRegion()).thenReturn(TestConstants.REGION);
        when(deliveryDetails.getCountry()).thenReturn(TestConstants.COUNTRY);
        when(ordersApi.getPaymentReference()).thenReturn(TestConstants.PAYMENT_REFERENCE);
        when(ordersApi.getOrderedAt()).thenReturn(TestConstants.TEST_DATE);
        when(ordersApi.getTotalOrderCost()).thenReturn(TestConstants.ORDER_COST);

        // when
        converter.mapOrder(ordersApi);

        // then
        assertEquals(TestConstants.ORDER_REFERENCE_NUMBER, converter.getEmailData().getOrderId());
        assertEquals("/GCI-2224/TODO", converter.getEmailData().getOrderSummaryLink());
        assertEquals(expectedEmailData(), converter.getEmailData());
    }

    private OrderNotificationEmailData expectedEmailData() {
        OrderNotificationEmailData result = new OrderNotificationEmailData();
        result.setOrderId(TestConstants.ORDER_REFERENCE_NUMBER);
        result.setPaymentDetails(PaymentDetails.builder()
                .withAmountPaid(TestConstants.ORDER_VIEW)
                .withPaymentReference(TestConstants.PAYMENT_REFERENCE)
                .withPaymentDate(TestConstants.PAYMENT_TIME)
                .build());
        result.setDeliveryDetails(DeliveryDetails.builder()
                .withAddressLine1(TestConstants.ADDRESS_LINE_1)
                .build());
        return result;
    }
}
