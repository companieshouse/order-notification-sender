package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@ExtendWith(MockitoExtension.class)
class CertificateOptionsMapperTest {
    @Mock
    private CertificateTypeMapper certificateTypeMapper;
    @Mock
    private AddressRecordTypeMapper addressRecordTypeMapper;
    @Mock
    private DeliveryMethodMapper deliveryMethodMapper;
    @Mock
    private DirectorOrSecretaryDetailsApiMapper directorOrSecretaryDetailsApiMapper;
    @Mock
    private OrdersApiDetails ordersApiDetails;
    @Mock
    private OrdersApiDetailsCommonFieldsMapper commonFieldsMapper;

    private OtherCertificateOptionsMapper otherCertificateOptionsMapper;

    @BeforeEach
    void setUp() {
        otherCertificateOptionsMapper = new OtherCertificateOptionsMapper(null, certificateTypeMapper,
                addressRecordTypeMapper, deliveryMethodMapper, directorOrSecretaryDetailsApiMapper,
                new CompanyStatusMapper(Collections.emptyMap(), new DefaultStatusMapper()), commonFieldsMapper);
    }

    @Test
    void testCertificateOrderNotificationMapperMapsSuccessfully() {
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
        itemOptions.setIncludeEmailCopy(false);

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
        when(ordersApiDetails.getCompanyName()).thenReturn(TestConstants.COMPANY_NAME);
        when(ordersApiDetails.getCompanyNumber()).thenReturn(TestConstants.COMPANY_NUMBER);
        when(ordersApiDetails.getItemOptions()).thenReturn(itemOptions);

        // when
        CertificateOrderNotificationModel result = otherCertificateOptionsMapper.generateEmailData(
                ordersApiDetails);

        // then
        CertificateOrderNotificationModel expected = new CertificateOrderNotificationModel();
        expected.setCompanyType(TestConstants.LIMITED_COMPANY_TYPE);
        expected.setCompanyName(TestConstants.COMPANY_NAME);
        expected.setCompanyNumber(TestConstants.COMPANY_NUMBER);
        expected.setCertificateType(TestConstants.CERTIFICATE_TYPE);
        expected.setStatementOfGoodStanding(new Content<>(TestConstants.READABLE_TRUE));
        expected.setDeliveryTimescale(TestConstants.DELIVERY_TIMESCALE);
        expected.setDeliveryMethod(TestConstants.DELIVERY_METHOD);
        expected.setEmailCopyRequired(TestConstants.EMAIL_COPY_EXPRESS_ONLY);
        expected.setRegisteredOfficeAddressDetails(TestConstants.EXPECTED_ADDRESS_TYPE);
        expected.setDirectorDetailsModel(getCertificateDetailsModel());
        expected.setSecretaryDetailsModel(getCertificateDetailsModel());
        expected.setCompanyObjects(TestConstants.READABLE_FALSE);

        assertEquals(expected, result);
    }

    @Test
    void testCertificateOrderNotificationMapperMapsSuccessfullyWhenStatusIsDissolved() {
        // given
        CertificateApi item = new CertificateApi();
        item.setCompanyName(TestConstants.COMPANY_NAME);
        item.setCompanyNumber(TestConstants.COMPANY_NUMBER);

        CertificateItemOptionsApi itemOptions = new CertificateItemOptionsApi();
        itemOptions.setCompanyType(TestConstants.LIMITED_COMPANY_TYPE);
        itemOptions.setCompanyStatus(TestConstants.DISSOLVED_STATUS);
        CertificateTypeApi certificateType = CertificateTypeApi.DISSOLUTION;
        itemOptions.setCertificateType(certificateType);
        itemOptions.setDeliveryMethod(DeliveryMethodApi.POSTAL);
        itemOptions.setDeliveryTimescale(DeliveryTimescaleApi.STANDARD);
        item.setItemOptions(itemOptions);

        when(certificateTypeMapper.mapCertificateType(any())).thenReturn(TestConstants.CERTIFICATE_TYPE);
        when(deliveryMethodMapper.mapDeliveryMethod(any(), any())).thenReturn(TestConstants.DELIVERY_METHOD);
        when(ordersApiDetails.getCompanyName()).thenReturn(TestConstants.COMPANY_NAME);
        when(ordersApiDetails.getCompanyNumber()).thenReturn(TestConstants.COMPANY_NUMBER);
        when(ordersApiDetails.getItemOptions()).thenReturn(itemOptions);

        // when
        CertificateOrderNotificationModel result = otherCertificateOptionsMapper.generateEmailData(
                ordersApiDetails);

        // then
        CertificateOrderNotificationModel expected = new CertificateOrderNotificationModel();
        expected.setCompanyType(TestConstants.LIMITED_COMPANY_TYPE);
        expected.setCompanyName(TestConstants.COMPANY_NAME);
        expected.setCompanyNumber(TestConstants.COMPANY_NUMBER);
        expected.setCertificateType(TestConstants.CERTIFICATE_TYPE);
        expected.setDeliveryMethod(TestConstants.DELIVERY_METHOD);

        assertEquals(expected, result);
        verifyNoInteractions(directorOrSecretaryDetailsApiMapper);
        verifyNoInteractions(addressRecordTypeMapper);
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
