package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.LiquidatorsDetailsApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

public class LiquidatorsDetailsApiMapperTest {

    @Test
    void testSetLiquidatorsDetailsToYesIfLiquidatorsDetailsTrue() {
        //given
        LiquidatorsDetailsApiMapper mapper = new LiquidatorsDetailsApiMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        liquidatorsDetailsApi.setIncludeBasicInformation(Boolean.TRUE);
        CertificateItemOptionsApi certificateItemOptionsApi = new CertificateItemOptionsApi();
        certificateItemOptionsApi.setLiquidatorsDetails(liquidatorsDetailsApi);
        certificateItemOptionsApi.setCompanyStatus("liquidation");

        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptionsApi, model);

        //then
        assertEquals(TestConstants.READABLE_TRUE, model.getLiquidatorsDetails());
        assertTrue(model.isRenderLiquidatorDetails());
        assertNull(model.getStatementOfGoodStanding());
        assertFalse(model.isRenderStatementOfGoodStanding());
    }

    @Test
    void testSetLiquidatorsDetailsToNoIfLiquidatorsDetailsFalse() {
        //given
        LiquidatorsDetailsApiMapper mapper = new LiquidatorsDetailsApiMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        liquidatorsDetailsApi.setIncludeBasicInformation(Boolean.FALSE);
        CertificateItemOptionsApi certificateItemOptionsApi = new CertificateItemOptionsApi();
        certificateItemOptionsApi.setLiquidatorsDetails(liquidatorsDetailsApi);
        certificateItemOptionsApi.setCompanyStatus("liquidation");

        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptionsApi, model);

        //then
        assertEquals(TestConstants.READABLE_FALSE, model.getLiquidatorsDetails());
        assertTrue(model.isRenderLiquidatorDetails());
        assertNull(model.getStatementOfGoodStanding());
        assertFalse(model.isRenderStatementOfGoodStanding());
    }

    @Test
    void testSetLiquidatorsDetailsToNullIfLiquidatorsDetailsAbsent() {
        //given
        LiquidatorsDetailsApiMapper mapper = new LiquidatorsDetailsApiMapper();
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setCompanyStatus("liquidation");

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertNull(model.getLiquidatorsDetails());
        assertFalse(model.isRenderLiquidatorDetails());
        assertNull(model.getStatementOfGoodStanding());
        assertFalse(model.isRenderStatementOfGoodStanding());
    }

    @Test
    void testSetLiquidatorsDetailsToNoIfLiquidatorsDetailsBasicInformationNull() {
        //given
        LiquidatorsDetailsApiMapper mapper = new LiquidatorsDetailsApiMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetailsApi);
        certificateItemOptions.setCompanyStatus("liquidation");
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertEquals(TestConstants.READABLE_FALSE, model.getLiquidatorsDetails());
        assertTrue(model.isRenderLiquidatorDetails());
        assertNull(model.getStatementOfGoodStanding());
        assertFalse(model.isRenderStatementOfGoodStanding());
    }

    @Test
    void testStatementOfGoodStandingRenderedAndSetToNoIfCompanyNotInLiquidationAndIncludeStatementOfGoodStandingIsFalse() {
        LiquidatorsDetailsApiMapper mapper = new LiquidatorsDetailsApiMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setIncludeGoodStandingInformation(false);
        certificateItemOptions.setCompanyStatus("active");
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertNull(model.getLiquidatorsDetails());
        assertFalse(model.isRenderLiquidatorDetails());
        assertEquals(TestConstants.READABLE_FALSE, model.getStatementOfGoodStanding());
        assertTrue(model.isRenderStatementOfGoodStanding());
    }

    @Test
    void testStatementOfGoodStandingRenderedAndSetToYesIfCompanyNotInLiquidationAndIncludeStatementOfGoodStandingIsTrue() {
        LiquidatorsDetailsApiMapper mapper = new LiquidatorsDetailsApiMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        certificateItemOptions.setCompanyStatus("active");
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertNull(model.getLiquidatorsDetails());
        assertFalse(model.isRenderLiquidatorDetails());
        assertEquals(TestConstants.READABLE_TRUE, model.getStatementOfGoodStanding());
        assertTrue(model.isRenderStatementOfGoodStanding());
    }
}