package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.CertificateApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryMethodApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;
import uk.gov.companieshouse.api.model.order.item.IncludeDobTypeApi;
import uk.gov.companieshouse.api.model.order.item.RegisteredOfficeAddressDetailsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CertificateOptionsMapperTest {
    @Mock
    private CertificateTypeMapper certificateTypeMapper;
    @Mock
    private AddressRecordTypeMapper roaTypeMapper;
    @Mock
    private DeliveryMethodMapper deliveryMethodMapper;
    @Mock
    private DirectorOrSecretaryDetailsApiMapper directorOrSecretaryDetailsApiMapper;

    @InjectMocks
    private OtherCertificateOptionsMapper otherCertificateOptionsMapper;

    @Test
    void testCertificateOrderNotificationMapperMapsSuccessfully() throws JsonProcessingException {
        // given
        DirectorOrSecretaryDetailsApi appointmentDetails = new DirectorOrSecretaryDetailsApi();
        appointmentDetails.setIncludeAddress(true);
        appointmentDetails.setIncludeAppointmentDate(true);
        appointmentDetails.setIncludeBasicInformation(true);
        appointmentDetails.setIncludeCountryOfResidence(true);
        appointmentDetails.setIncludeDobType(IncludeDobTypeApi.FULL);
        appointmentDetails.setIncludeNationality(null);
        appointmentDetails.setIncludeOccupation(false);

        CertificateApi item = new CertificateApi();
        item.setCompanyName(TestConstants.COMPANY_NAME);
        item.setCompanyNumber(TestConstants.COMPANY_NUMBER);

        CertificateItemOptionsApi itemOptions = new CertificateItemOptionsApi();
        itemOptions.setCompanyType(TestConstants.LIMITED_COMPANY_TYPE);
        CertificateTypeApi certificateType = CertificateTypeApi.INCORPORATION;
        itemOptions.setCertificateType(certificateType);
        itemOptions.setIncludeGoodStandingInformation(true);
        itemOptions.setDeliveryMethod(DeliveryMethodApi.POSTAL);
        itemOptions.setDeliveryTimescale(DeliveryTimescaleApi.STANDARD);

        RegisteredOfficeAddressDetailsApi registeredOfficeAddressDetails = new RegisteredOfficeAddressDetailsApi();
        IncludeAddressRecordsTypeApi addressRecord = IncludeAddressRecordsTypeApi.CURRENT_PREVIOUS_AND_PRIOR;
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(addressRecord);
        registeredOfficeAddressDetails.setIncludeDates(true);

        itemOptions.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetails);
        itemOptions.setDirectorDetails(appointmentDetails);
        itemOptions.setSecretaryDetails(appointmentDetails);
        itemOptions.setIncludeCompanyObjectsInformation(false);
        item.setItemOptions(itemOptions);

        when(certificateTypeMapper.mapCertificateType(any())).thenReturn(TestConstants.CERTIFICATE_TYPE);
        when(roaTypeMapper.mapAddressRecordType(any())).thenReturn(TestConstants.EXPECTED_ADDRESS_TYPE);
        when(deliveryMethodMapper.mapDeliveryMethod(any(), any())).thenReturn(TestConstants.DELIVERY_METHOD);
//        when(directorOrSecretaryDetailsApiMapper.getAddress()).thenReturn(TestConstants.ADDRESS_TYPE);
//        when(directorOrSecretaryDetailsApiMapper.getAppointmentDate()).thenReturn(TestConstants.APPOINTMENT_DATE);
//        when(directorOrSecretaryDetailsApiMapper.getDob()).thenReturn(TestConstants.DOB_TYPE);
//        when(directorOrSecretaryDetailsApiMapper.getCountryOfResidence()).thenReturn(TestConstants.COUNTRY_OF_RESIDENCE);
//        when(directorOrSecretaryDetailsApiMapper.getNationality()).thenReturn(TestConstants.NATIONALITY);
//        when(directorOrSecretaryDetailsApiMapper.getOccupation()).thenReturn(TestConstants.OCCUPATION);

        // when
        CertificateOrderNotificationModel result = otherCertificateOptionsMapper.generateEmailData(item);

        // then
        assertEquals(getExpectedModel(), result);
    }

    private CertificateOrderNotificationModel getExpectedModel() {
        CertificateOrderNotificationModel expected = new CertificateOrderNotificationModel();
        expected.setCompanyType(TestConstants.LIMITED_COMPANY_TYPE);
        expected.setCompanyName(TestConstants.COMPANY_NAME);
        expected.setCompanyNumber(TestConstants.COMPANY_NUMBER);
        expected.setCertificateType(TestConstants.CERTIFICATE_TYPE);
        expected.setStatementOfGoodStanding(TestConstants.READABLE_TRUE);
        expected.setDeliveryMethod(TestConstants.DELIVERY_METHOD);
        expected.setRegisteredOfficeAddressDetails(TestConstants.EXPECTED_ADDRESS_TYPE);
        expected.setDirectorDetailsModel(getAppointmentDetails());
        expected.setSecretaryDetailsModel(getAppointmentDetails());
        expected.setCompanyObjects(TestConstants.READABLE_FALSE);
        return expected;
    }

    private CertificateDetailsModel getAppointmentDetails() {
        return new CertificateDetailsModel(true, Collections.singletonList("Yes"));
    }
}
