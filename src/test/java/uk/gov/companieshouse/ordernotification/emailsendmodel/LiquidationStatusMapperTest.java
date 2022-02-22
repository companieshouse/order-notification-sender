package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.LiquidatorsDetailsApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class LiquidationStatusMapperTest {

    @Test
    void testSetLiquidatorsDetailsToYesIfLiquidatorsDetailsTrue() {
        //given
        LiquidationStatusMapper mapper = new LiquidationStatusMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        liquidatorsDetailsApi.setIncludeBasicInformation(Boolean.TRUE);
        CertificateItemOptionsApi certificateItemOptionsApi = new CertificateItemOptionsApi();
        certificateItemOptionsApi.setLiquidatorsDetails(liquidatorsDetailsApi);
        certificateItemOptionsApi.setCompanyStatus("liquidation");

        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptionsApi, model);

        //then
        assertEquals(new Content<>(TestConstants.READABLE_TRUE), model.getLiquidatorsDetails());
    }

    @Test
    void testSetLiquidatorsDetailsToNoIfLiquidatorsDetailsFalse() {
        //given
        LiquidationStatusMapper mapper = new LiquidationStatusMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        liquidatorsDetailsApi.setIncludeBasicInformation(Boolean.FALSE);
        CertificateItemOptionsApi certificateItemOptionsApi = new CertificateItemOptionsApi();
        certificateItemOptionsApi.setLiquidatorsDetails(liquidatorsDetailsApi);
        certificateItemOptionsApi.setCompanyStatus("liquidation");

        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptionsApi, model);

        //then
        assertEquals(new Content<>(TestConstants.READABLE_FALSE), model.getLiquidatorsDetails());
    }

    @Test
    void testSetLiquidatorsDetailsToNullIfLiquidatorsDetailsAbsent() {
        //given
        LiquidationStatusMapper mapper = new LiquidationStatusMapper();
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setCompanyStatus("liquidation");

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertNull(model.getLiquidatorsDetails());
    }

    @Test
    void testSetLiquidatorsDetailsToNoIfLiquidatorsDetailsBasicInformationNull() {
        //given
        LiquidationStatusMapper mapper = new LiquidationStatusMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetailsApi);
        certificateItemOptions.setCompanyStatus("liquidation");
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertEquals(new Content<>(TestConstants.READABLE_FALSE), model.getLiquidatorsDetails());
    }
}
