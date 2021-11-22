package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;
import uk.gov.companieshouse.api.model.order.item.IncludeDobTypeApi;
import uk.gov.companieshouse.api.model.order.item.RegisteredOfficeAddressDetailsApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtherCertificateOptionsMapperTest {

    @Mock
    private AddressRecordTypeMapper addressRecordTypeMapper;
    @Mock
    private DirectorOrSecretaryDetailsApiMapper directorOrSecretaryDetailsApiMapper;
    @Mock
    private LiquidatorsDetailsApiMapper liquidatorsDetailsApiMapper;

    @InjectMocks
    private OtherCertificateOptionsMapper otherCertificateOptionsMapper;

    @Test
    void doMapCustomDataMapsCorrectly() {
        // given
        DirectorOrSecretaryDetailsApi appointmentDetails = new DirectorOrSecretaryDetailsApi();
        appointmentDetails.setIncludeAddress(true);
        appointmentDetails.setIncludeAppointmentDate(true);
        appointmentDetails.setIncludeBasicInformation(true);
        appointmentDetails.setIncludeCountryOfResidence(true);
        appointmentDetails.setIncludeDobType(IncludeDobTypeApi.FULL);
        appointmentDetails.setIncludeNationality(true);
        appointmentDetails.setIncludeOccupation(true);

        CertificateItemOptionsApi itemOptions = new CertificateItemOptionsApi();

        RegisteredOfficeAddressDetailsApi registeredOfficeAddressDetails = new RegisteredOfficeAddressDetailsApi();
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(IncludeAddressRecordsTypeApi.CURRENT_PREVIOUS_AND_PRIOR);

        itemOptions.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetails);
        itemOptions.setDirectorDetails(appointmentDetails);
        itemOptions.setSecretaryDetails(appointmentDetails);
        itemOptions.setIncludeCompanyObjectsInformation(false);

        CertificateOrderNotificationModel result = new CertificateOrderNotificationModel();

        when(addressRecordTypeMapper.mapAddressRecordType(any())).thenReturn(TestConstants.ADDRESS_TYPE);
        when(directorOrSecretaryDetailsApiMapper.map(any())).thenReturn(getCertificateDetailsModel());

        // when
        otherCertificateOptionsMapper.doMapCustomData(itemOptions, result);

        // then
        assertEquals(getCertificateOrderNotificationModel(), result);
        verify(liquidatorsDetailsApiMapper).map(eq(itemOptions), any());
    }

    private CertificateOrderNotificationModel getCertificateOrderNotificationModel() {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        model.setDirectorDetailsModel(getCertificateDetailsModel());
        model.setSecretaryDetailsModel(getCertificateDetailsModel());
        model.setRegisteredOfficeAddressDetails(TestConstants.ADDRESS_TYPE);
        model.setCompanyObjects("No");
        return model;
    }
    private CertificateDetailsModel getCertificateDetailsModel() {
        return new CertificateDetailsModel(true, new ArrayList<String>() {
            {
                add("Correspondence address");
                add("Appointment date");
                add("Country of residence");
                add("Nationality");
                add("Occupation");
                add("Date of birth (month and year)");
            }
        });
    }

}
