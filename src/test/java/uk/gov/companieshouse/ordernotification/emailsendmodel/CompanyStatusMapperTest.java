package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.LiquidatorsDetailsApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

class CompanyStatusMapperTest {

    @Test
    void testSetLiquidatorsDetailsToYesIfLiquidatorsDetailsTrue() {
        //given
        CompanyStatusMapper mapper = new CompanyStatusMapper();
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
        assertNull(model.getStatementOfGoodStanding());
    }

    @Test
    void testSetLiquidatorsDetailsToNoIfLiquidatorsDetailsFalse() {
        //given
        CompanyStatusMapper mapper = new CompanyStatusMapper();
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
        assertNull(model.getStatementOfGoodStanding());
    }

    @Test
    void testSetLiquidatorsDetailsToNullIfLiquidatorsDetailsAbsent() {
        //given
        CompanyStatusMapper mapper = new CompanyStatusMapper();
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setCompanyStatus("liquidation");

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertNull(model.getLiquidatorsDetails());
        assertNull(model.getStatementOfGoodStanding());
    }

    @Test
    void testSetLiquidatorsDetailsToNoIfLiquidatorsDetailsBasicInformationNull() {
        //given
        CompanyStatusMapper mapper = new CompanyStatusMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setLiquidatorsDetails(liquidatorsDetailsApi);
        certificateItemOptions.setCompanyStatus("liquidation");
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertEquals(new Content<>(TestConstants.READABLE_FALSE), model.getLiquidatorsDetails());
        assertNull(model.getStatementOfGoodStanding());
    }

    @Test
    void testStatementOfGoodStandingRenderedAndSetToNoIfCompanyNotInLiquidationAndIncludeStatementOfGoodStandingIsFalse() {
        CompanyStatusMapper mapper = new CompanyStatusMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setIncludeGoodStandingInformation(false);
        certificateItemOptions.setCompanyStatus("active");
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertNull(model.getLiquidatorsDetails());
        assertEquals(new Content<>(TestConstants.READABLE_FALSE), model.getStatementOfGoodStanding());
    }

    @Test
    void testStatementOfGoodStandingRenderedAndSetToYesIfCompanyNotInLiquidationAndIncludeStatementOfGoodStandingIsTrue() {
        CompanyStatusMapper mapper = new CompanyStatusMapper();
        LiquidatorsDetailsApi liquidatorsDetailsApi = new LiquidatorsDetailsApi();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        certificateItemOptions.setCompanyStatus("active");
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertNull(model.getLiquidatorsDetails());
        assertEquals(new Content<>(TestConstants.READABLE_TRUE), model.getStatementOfGoodStanding());
    }
}