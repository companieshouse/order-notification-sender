package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

public class EmailRequiredMapperTest {

    @Test
    void testReturnValueForMapIsEmailRequiredStandardDelivery() {

        CertificateItemOptionsApi itemOptionsApi = new CertificateItemOptionsApi();

        itemOptionsApi.setDeliveryTimescale(DeliveryTimescaleApi.STANDARD);
        itemOptionsApi.setIncludeEmailCopy(false);

        //when
        String actual = EmailRequiredMapper.mapIsEmailRequired(itemOptionsApi);

        //then
        assertEquals(TestConstants.EMAIL_COPY_EXPRESS_ONLY, actual);
    }

    @Test
    void testReturnValueForMapIsEmailRequiredExpressDeliveryEmailIsRequired() {

        CertificateItemOptionsApi itemOptionsApi = new CertificateItemOptionsApi();

        itemOptionsApi.setDeliveryTimescale(DeliveryTimescaleApi.SAME_DAY);
        itemOptionsApi.setIncludeEmailCopy(true);

        //when
        String actual = EmailRequiredMapper.mapIsEmailRequired(itemOptionsApi);

        //then
        assertEquals(TestConstants.READABLE_TRUE, actual);
    }

    @Test
    void testReturnValueForMapIsEmailRequiredExpressDeliveryEmailIsNotRequired() {

        CertificateItemOptionsApi itemOptionsApi = new CertificateItemOptionsApi();

        itemOptionsApi.setDeliveryTimescale(DeliveryTimescaleApi.SAME_DAY);
        itemOptionsApi.setIncludeEmailCopy(false);

        //when
        String actual = EmailRequiredMapper.mapIsEmailRequired(itemOptionsApi);

        //then
        assertEquals(TestConstants.READABLE_FALSE, actual);
    }
}
