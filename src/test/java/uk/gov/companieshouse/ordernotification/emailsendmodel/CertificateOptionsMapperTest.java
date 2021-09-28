package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import uk.gov.companieshouse.api.model.order.item.CertificateApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryMethodApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;
import uk.gov.companieshouse.api.model.order.item.IncludeDobTypeApi;
import uk.gov.companieshouse.api.model.order.item.RegisteredOfficeAddressDetailsApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CertificateOptionsMapperTest {
    @Mock
    private CertificateTypeMapper certificateTypeMapper;
    @Mock
    private AddressRecordTypeMapper addressRecordTypeMapper;
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
        appointmentDetails.setIncludeNationality(true);
        appointmentDetails.setIncludeOccupation(true);

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
        when(addressRecordTypeMapper.mapAddressRecordType(any())).thenReturn(TestConstants.EXPECTED_ADDRESS_TYPE);
        when(deliveryMethodMapper.mapDeliveryMethod(any(), any())).thenReturn(TestConstants.DELIVERY_METHOD);

        when(directorOrSecretaryDetailsApiMapper.map(any())).thenReturn(getCertificateDetailsModel());

        // when
        CertificateOrderNotificationModel result = otherCertificateOptionsMapper.generateEmailData(item);

        // then
        CertificateOrderNotificationModel expected = new CertificateOrderNotificationModel();
        expected.setCompanyType(TestConstants.LIMITED_COMPANY_TYPE);
        expected.setCompanyName(TestConstants.COMPANY_NAME);
        expected.setCompanyNumber(TestConstants.COMPANY_NUMBER);
        expected.setCertificateType(TestConstants.CERTIFICATE_TYPE);
        expected.setStatementOfGoodStanding(TestConstants.READABLE_TRUE);
        expected.setDeliveryMethod(TestConstants.DELIVERY_METHOD);
        expected.setRegisteredOfficeAddressDetails(TestConstants.EXPECTED_ADDRESS_TYPE);
        expected.setDirectorDetailsModel(getCertificateDetailsModel());
        expected.setSecretaryDetailsModel(getCertificateDetailsModel());
        expected.setCompanyObjects(TestConstants.READABLE_FALSE);

        assertEquals(expected, result);
    }

    private CertificateDetailsModel getCertificateDetailsModel() {
        return new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>() {
            {
                add("Correspondence address");
                add("Appointment date");
                add("Country of residence");
                add("Nationality");
                add("Occupation");
                add("Date of birth (month and year)");
            }
        }));
    }
}
