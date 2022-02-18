package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DefaultStatusMapperTest {

    @Test
    void testStatementOfGoodStandingIsNoIfIncludeStatementOfGoodStandingIsFalse() {
        DefaultStatusMapper mapper = new DefaultStatusMapper();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setIncludeGoodStandingInformation(false);
        certificateItemOptions.setCompanyStatus("active");
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertEquals(new Content<>(TestConstants.READABLE_FALSE), model.getStatementOfGoodStanding());
    }

    @Test
    void testStatementOfGoodStandingIsYesIfIncludeStatementOfGoodStandingIsTrue() {
        DefaultStatusMapper mapper = new DefaultStatusMapper();
        CertificateItemOptionsApi certificateItemOptions = new CertificateItemOptionsApi();
        certificateItemOptions.setIncludeGoodStandingInformation(true);
        certificateItemOptions.setCompanyStatus("active");
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();

        //when
        mapper.map(certificateItemOptions, model);

        //then
        assertEquals(new Content<>(TestConstants.READABLE_TRUE), model.getStatementOfGoodStanding());
    }
}
