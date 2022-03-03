package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.AdministratorsDetailsApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AdministrationStatusMapperTest {

    @Test
    void testSetAdministratorsDetailsToNoIfAdministratorDetailsFalse() {
        //given
        CertificateItemOptionsApi source = new CertificateItemOptionsApi();
        AdministratorsDetailsApi administratorsDetailsApi = new AdministratorsDetailsApi();
        administratorsDetailsApi.setIncludeBasicInformation(Boolean.FALSE);
        source.setAdministratorsDetails(administratorsDetailsApi);
        CertificateOrderNotificationModel target = new CertificateOrderNotificationModel();
        AdministrationStatusMapper administrationStatusMapper = new AdministrationStatusMapper();

        //when
        administrationStatusMapper.map(source, target);

        //then
        assertEquals("No", target.getAdministratorsDetails().getContent());
    }

    @Test
    void testSetAdministratorsDetailsToYesIfAdministratorDetailsTrue() {
        //given
        CertificateItemOptionsApi source = new CertificateItemOptionsApi();
        AdministratorsDetailsApi administratorsDetailsApi = new AdministratorsDetailsApi();
        administratorsDetailsApi.setIncludeBasicInformation(Boolean.TRUE);
        source.setAdministratorsDetails(administratorsDetailsApi);
        CertificateOrderNotificationModel target = new CertificateOrderNotificationModel();
        AdministrationStatusMapper administrationStatusMapper = new AdministrationStatusMapper();

        //when
        administrationStatusMapper.map(source, target);

        //then
        assertEquals("Yes", target.getAdministratorsDetails().getContent());
    }

    @Test
    void testSetAdministratorsDetailsToNullIfAdministratorsDetailsAbsent() {
        //given
        CertificateItemOptionsApi source = new CertificateItemOptionsApi();
        CertificateOrderNotificationModel target = new CertificateOrderNotificationModel();
        AdministrationStatusMapper administrationStatusMapper = new AdministrationStatusMapper();

        //when
        administrationStatusMapper.map(source, target);

        //then
        assertNull(target.getAdministratorsDetails());
    }

    @Test
    void testSetAdministratorsDetailsToNoIfAdministratorDetailsNull() {
        //given
        CertificateItemOptionsApi source = new CertificateItemOptionsApi();
        AdministratorsDetailsApi administratorsDetailsApi = new AdministratorsDetailsApi();
        source.setAdministratorsDetails(administratorsDetailsApi);
        CertificateOrderNotificationModel target = new CertificateOrderNotificationModel();
        AdministrationStatusMapper administrationStatusMapper = new AdministrationStatusMapper();

        //when
        administrationStatusMapper.map(source, target);

        //then
        assertEquals("No", target.getAdministratorsDetails().getContent());
    }
}
