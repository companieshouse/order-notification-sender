package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.LiquidatorsDetailsApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LiquidatorsDetailsApiMapperTest {

    @Test
    void testSetLiquidatorsDetailsToYesIfLiquidatorsDetailsTrue() {
        //given
        LiquidatorsDetailsApiMapper mapper = new LiquidatorsDetailsApiMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        liquidatorsDetailsApi.setIncludeBasicInformation(Boolean.TRUE);
        CertificateItemOptionsApi certificateItemOptionsApi = new CertificateItemOptionsApi();
        certificateItemOptionsApi.setLiquidatorsDetails(liquidatorsDetailsApi);

        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptionsApi, model);

        //then
        assertEquals(TestConstants.READABLE_TRUE, model.getLiquidatorsDetails());
    }

    @Test
    void testSetLiquidatorsDetailsToNoIfLiquidatorsDetailsFalse() {
        //given
        LiquidatorsDetailsApiMapper mapper = new LiquidatorsDetailsApiMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        liquidatorsDetailsApi.setIncludeBasicInformation(Boolean.FALSE);
        CertificateItemOptionsApi certificateItemOptionsApi = new CertificateItemOptionsApi();
        certificateItemOptionsApi.setLiquidatorsDetails(liquidatorsDetailsApi);

        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptionsApi, model);

        //then
        assertEquals(TestConstants.READABLE_FALSE, model.getLiquidatorsDetails());
    }

    @Test
    void testSetLiquidatorsDetailsToNullIfLiquidatorsDetailsAbsent() {
        //given
        LiquidatorsDetailsApiMapper mapper = new LiquidatorsDetailsApiMapper();
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(new CertificateItemOptionsApi(), model);

        //then
        assertNull(model.getLiquidatorsDetails());
    }

    @Test
    void testSetLiquidatorsDetailsToNullIfLiquidatorsDetailsBasicInformationNull() {
        //given
        LiquidatorsDetailsApiMapper mapper = new LiquidatorsDetailsApiMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetailsApi);
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertNull(model.getLiquidatorsDetails());
    }
}
