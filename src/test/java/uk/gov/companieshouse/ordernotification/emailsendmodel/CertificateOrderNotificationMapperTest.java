package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.CertificateApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;
import uk.gov.companieshouse.api.model.order.item.IncludeDobTypeApi;
import uk.gov.companieshouse.api.model.order.item.RegisteredOfficeAddressDetailsApi;
import uk.gov.companieshouse.ordernotification.orders.model.CertificateType;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class CertificateOrderNotificationMapperTest {

    private CertificateOrderNotificationMapper certificateOrderNotificationMapper;

    @Mock
    private DateGenerator dateGenerator;

    @BeforeEach
    void setup() {
        certificateOrderNotificationMapper = new CertificateOrderNotificationMapper(dateGenerator, "dd MMMM yyyy", "noreply@companieshouse.gov.uk");
    }

    @Test
    void testCertificateOrderNotificationMapperMapsSuccessfully() {
        // given
        OrdersApi order = getOrder("ACME Limited", "12345678", "£15.00", "ABCD-EFGH-IJKL");

        // when
        CertificateOrderNotificationModel result = certificateOrderNotificationMapper.generateEmailData(order);

        // then
        assertEquals(result.getCompanyName(), "ACME Limited");
        assertEquals(result.getCompanyNumber(), "12345678");
        assertEquals(result.getAmountPaid(), "£15.00");
        assertEquals(result.getPaymentReference(), "ABCD-EFGH-IJKL");

        // TODO: Flesh out assertEquals
    }

    private OrdersApi getOrder(String companyName, String companyNumber, String orderCost, String paymentReference) {
        OrdersApi order = new OrdersApi();

        CertificateApi item = new CertificateApi();
        item.setCompanyName(companyName);
        item.setCompanyNumber(companyNumber);

        CertificateItemOptionsApi itemOptions = new CertificateItemOptionsApi();

        CertificateTypeApi certificateType = CertificateTypeApi.INCORPORATION;
        itemOptions.setCertificateType(certificateType);
        itemOptions.setIncludeGoodStandingInformation(true);
        RegisteredOfficeAddressDetailsApi registeredOfficeAddressDetails = new RegisteredOfficeAddressDetailsApi();
        IncludeAddressRecordsTypeApi addressRecord = IncludeAddressRecordsTypeApi.CURRENT_PREVIOUS_AND_PRIOR;
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(addressRecord);
        registeredOfficeAddressDetails.setIncludeDates(true);
        itemOptions.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetails);

        DirectorOrSecretaryDetailsApi directorDetails = new DirectorOrSecretaryDetailsApi();
        directorDetails.setIncludeAddress(true);
        directorDetails.setIncludeAppointmentDate(true);
        directorDetails.setIncludeBasicInformation(true);
        directorDetails.setIncludeCountryOfResidence(true);
        IncludeDobTypeApi directorDobType = IncludeDobTypeApi.FULL;
        directorDetails.setIncludeDobType(directorDobType);
        directorDetails.setIncludeNationality(true);
        directorDetails.setIncludeOccupation(true);
        itemOptions.setDirectorDetails(directorDetails);

        DirectorOrSecretaryDetailsApi secretaryDetails = new DirectorOrSecretaryDetailsApi();
        secretaryDetails.setIncludeAddress(true);
        secretaryDetails.setIncludeAppointmentDate(true);
        secretaryDetails.setIncludeBasicInformation(true);
        secretaryDetails.setIncludeCountryOfResidence(true);
        IncludeDobTypeApi secretaryDobType = IncludeDobTypeApi.FULL;
        secretaryDetails.setIncludeDobType(secretaryDobType);
        secretaryDetails.setIncludeNationality(true);
        secretaryDetails.setIncludeOccupation(true);
        itemOptions.setSecretaryDetails(secretaryDetails);

        itemOptions.setIncludeCompanyObjectsInformation(true);

        item.setItemOptions(itemOptions);
        order.setItems(Collections.singletonList(item));
        order.setTotalOrderCost(orderCost);
        order.setPaymentReference(paymentReference);

        return order;
    }

}
