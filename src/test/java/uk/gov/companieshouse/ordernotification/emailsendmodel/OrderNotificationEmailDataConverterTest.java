package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.DeliveryDetailsApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.CertificateApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

@ExtendWith(MockitoExtension.class)
class OrderNotificationEmailDataConverterTest {

    private OrderNotificationEmailDataConverter converter;

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
    private OrdersApi ordersApi;

    @Mock
    private DeliveryDetailsApi deliveryDetails;

    @Mock
    private CertificateApi certificateApi;

    @Mock
    private CertificateItemOptionsApi certificateItemOptionsApi;

    @Mock
    private Certificate certificate;

    @Mock
    private CertifiedCopyApi certifiedCopyApi;

    @Mock
    private CertifiedCopyItemOptionsApi certifiedCopyItemOptionsApi;

    @Mock
    private CertifiedCopy certifiedCopy;

    @Mock
    private MissingImageDeliveryApi missingImageDeliveryApi;

    @Mock
    private MissingImageDelivery missingImageDelivery;

    @BeforeEach
    void setup() {
        emailData = new OrderNotificationEmailData();
        converter = new OrderNotificationEmailDataConverter(emailData, certificateEmailDataMapper,
                certifiedCopyEmailDataMapper, missingImageDeliveryEmailDataMapper, emailConfiguration);
    }

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
        when(deliveryDetails.getPostalCode()).thenReturn(TestConstants.POSTAL_CODE);
        when(deliveryDetails.getCountry()).thenReturn(TestConstants.COUNTRY);
        when(ordersApi.getPaymentReference()).thenReturn(TestConstants.PAYMENT_REFERENCE);
        when(ordersApi.getOrderedAt()).thenReturn(TestConstants.TEST_DATE);
        when(ordersApi.getTotalOrderCost()).thenReturn(TestConstants.ORDER_COST);
        when(emailConfiguration.getPaymentDateFormat()).thenReturn(TestConstants.PAYMENT_DATE_FORMAT);
        when(emailConfiguration.getChsUrl()).thenReturn(TestConstants.CHS_URL);
        when(emailConfiguration.getDispatchDays()).thenReturn(10);

        // when
        converter.mapOrder(ordersApi);

        // then
        assertEquals(TestConstants.ORDER_REFERENCE_NUMBER, converter.getEmailData().getOrderId());
        assertEquals(expectedEmailData(), converter.getEmailData());
        assertEquals(10, converter.getEmailData().getDispatchDays());
    }

    @Test
    void testMapCertificateStandardDelivery() {
        // given
        when(certificateEmailDataMapper.map(any())).thenReturn(certificate);
        when(certificateApi.getItemOptions()).thenReturn(certificateItemOptionsApi);
        when(certificateItemOptionsApi.getDeliveryTimescale()).thenReturn(DeliveryTimescaleApi.STANDARD);
        // when
        converter.mapCertificate(certificateApi);

        // then
        assertTrue(converter.getEmailData().getCertificates().contains(certificate));
        assertTrue(converter.getEmailData().hasStandardDelivery());
        verify(certificateEmailDataMapper).map(certificateApi);
    }

    @Test
    void testMapCertificateExpressDelivery() {
        // given
        when(certificateEmailDataMapper.map(any())).thenReturn(certificate);
        when(certificateApi.getItemOptions()).thenReturn(certificateItemOptionsApi);
        when(certificateItemOptionsApi.getDeliveryTimescale()).thenReturn(DeliveryTimescaleApi.SAME_DAY);
        // when
        converter.mapCertificate(certificateApi);

        // then
        assertTrue(converter.getEmailData().getCertificates().contains(certificate));
        assertTrue(converter.getEmailData().hasExpressDelivery());
        verify(certificateEmailDataMapper).map(certificateApi);
    }

    @Test
    void testMapCertifiedCopyStandardDelivery() {
        // given
        when(certifiedCopyEmailDataMapper.map(any())).thenReturn(certifiedCopy);
        when(certifiedCopyApi.getItemOptions()).thenReturn(certifiedCopyItemOptionsApi);
        when(certifiedCopyItemOptionsApi.getDeliveryTimescale()).thenReturn(DeliveryTimescaleApi.STANDARD);
        // when
        converter.mapCertifiedCopy(certifiedCopyApi);

        // then
        assertTrue(converter.getEmailData().getCertifiedCopies().contains(certifiedCopy));
        assertTrue(converter.getEmailData().hasStandardDelivery());
        verify(certifiedCopyEmailDataMapper).map(certifiedCopyApi);
    }

    @Test
    void testMapCertifiedCopyExpressDelivery() {
        // given
        when(certifiedCopyEmailDataMapper.map(any())).thenReturn(certifiedCopy);
        when(certifiedCopyApi.getItemOptions()).thenReturn(certifiedCopyItemOptionsApi);
        when(certifiedCopyItemOptionsApi.getDeliveryTimescale()).thenReturn(DeliveryTimescaleApi.SAME_DAY);
        // when
        converter.mapCertifiedCopy(certifiedCopyApi);

        // then
        assertTrue(converter.getEmailData().getCertifiedCopies().contains(certifiedCopy));
        assertTrue(converter.getEmailData().hasExpressDelivery());
        verify(certifiedCopyEmailDataMapper).map(certifiedCopyApi);
    }

    @Test
    void testMapMissingImageDelivery() {
        // given
        when(missingImageDeliveryEmailDataMapper.map(any())).thenReturn(missingImageDelivery);
        // when
        converter.mapMissingImageDelivery(missingImageDeliveryApi);

        // then
        assertTrue(converter.getEmailData().getMissingImageDeliveries().contains(missingImageDelivery));
        verify(missingImageDeliveryEmailDataMapper).map(missingImageDeliveryApi);
    }

    private OrderNotificationEmailData expectedEmailData() {
        OrderNotificationEmailData result = new OrderNotificationEmailData();
        result.setOrderSummaryLink(String.format("%s/orders/%s", TestConstants.CHS_URL, TestConstants.ORDER_REFERENCE_NUMBER));
        result.setOrderId(TestConstants.ORDER_REFERENCE_NUMBER);
        result.setPaymentDetails(PaymentDetails.builder()
                .withAmountPaid(TestConstants.ORDER_VIEW)
                .withPaymentReference(TestConstants.PAYMENT_REFERENCE)
                .withPaymentDate(TestConstants.PAYMENT_TIME)
                .build());
        result.setDeliveryDetails(DeliveryDetails.builder()
                .withForename(TestConstants.FORENAME)
                .withSurname(TestConstants.SURNAME)
                .withAddressLine1(TestConstants.ADDRESS_LINE_1)
                .withAddressLine2(TestConstants.ADDRESS_LINE_2)
                .withPoBox(TestConstants.PO_BOX)
                .withLocality(TestConstants.LOCALITY)
                .withRegion(TestConstants.REGION)
                .withPostalCode(TestConstants.POSTAL_CODE)
                .withCountry(TestConstants.COUNTRY)
                .build());
        result.setDispatchDays(10);
        return result;
    }
}
