package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.CertificateApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;
import uk.gov.companieshouse.api.model.order.item.IncludeDobTypeApi;
import uk.gov.companieshouse.api.model.order.item.RegisteredOfficeAddressDetailsApi;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

@ExtendWith(MockitoExtension.class)
public class CertificateOrderNotificationMapperTest {

    private CertificateOrderNotificationMapper certificateOrderNotificationMapper;

    @Mock
    private DateGenerator dateGenerator;

    @BeforeEach
    void setup() {
        certificateOrderNotificationMapper = new CertificateOrderNotificationMapper(dateGenerator,
                TestConstants.EMAIL_DATE_FORMAT, TestConstants.SENDER_EMAIL_ADDRESS, TestConstants.PAYMENT_DATE_FORMAT,
                TestConstants.MESSAGE_ID, TestConstants.APPLICATION_ID, TestConstants.MESSAGE_TYPE);
    }

    @Test
    void testCertificateOrderNotificationMapperMapsSuccessfully() throws JsonProcessingException {
        // given
        OrdersApi order = getOrder(getAppointmentApiDetails(IncludeDobTypeApi.FULL));
        when(dateGenerator.generate()).thenReturn(LocalDateTime.of(2021, 7, 27, 15, 20, 10));

        // when
        EmailSend result = certificateOrderNotificationMapper.map(order);

        // then
        assertEquals(getExpectedEmailSendModel(getAppointmentDetails(TestConstants.DOB_TYPE)), result);
    }

    @Test
    void testCertificateOrderNotificationMapperReturnsMessageId() {
        //when
        String actual = certificateOrderNotificationMapper.getMessageId();

        //then
        assertEquals(TestConstants.MESSAGE_ID, actual);
    }

    @Test
    void testCertificateOrderNotificationMapperReturnsApplicationId() {
        //when
        String actual = certificateOrderNotificationMapper.getApplicationId();

        //then
        assertEquals(TestConstants.APPLICATION_ID, actual);
    }

    @Test
    void testCertificateOrderNotificationMapperReturnsMessageType() {
        //when
        String actual = certificateOrderNotificationMapper.getMessageType();

        //then
        assertEquals(TestConstants.MESSAGE_TYPE, actual);
    }

    @Test
    void testMapperSkipsDobTypeIfNotProvided() throws JsonProcessingException {
        //given
        OrdersApi order = getOrder(getAppointmentApiDetails(null));
        when(dateGenerator.generate()).thenReturn(LocalDateTime.of(2021, 7, 27, 15, 20, 10));

        //when
        EmailSend result = certificateOrderNotificationMapper.map(order);

        // then
        assertEquals(getExpectedEmailSendModel(getAppointmentDetails(null)), result);
    }

    private EmailSend getExpectedEmailSendModel(CertificateAppointmentDetailsModel appointmentDetailsModel) throws JsonProcessingException {
        EmailSend expected = new EmailSend();
        expected.setAppId(TestConstants.APPLICATION_ID);
        OrderModel model = getExpectedModel(appointmentDetailsModel);
        model.setOrderReferenceNumber(TestConstants.ORDER_REFERENCE_NUMBER);
        model.setPaymentReference(TestConstants.PAYMENT_REFERENCE);
        model.setPaymentTime("27 July 2021 - 15:20:10");
        model.setTotalOrderCost(TestConstants.ORDER_COST);
        expected.setData(new ObjectMapper().writeValueAsString(model));
        expected.setCreatedAt("27 July 2021");
        expected.setEmailAddress("noreply@companieshouse.gov.uk");
        expected.setMessageId(TestConstants.MESSAGE_ID);
        expected.setMessageType(TestConstants.MESSAGE_TYPE);
        return expected;
    }

    private CertificateOrderNotificationModel getExpectedModel(CertificateAppointmentDetailsModel appointmentDetailsModel) {
        CertificateOrderNotificationModel expected = new CertificateOrderNotificationModel();
        expected.setCompanyName(TestConstants.COMPANY_NAME);
        expected.setCompanyNumber(TestConstants.COMPANY_NUMBER);
        expected.setCertificateType(TestConstants.CERTIFICATE_TYPE);
        expected.setStatementOfGoodStanding(true);
        expected.setCertificateRegisteredOfficeAddressModel(new CertificateRegisteredOfficeAddressModel(TestConstants.ADDRESS_TYPE, true));
        expected.setDirectorDetailsModel(appointmentDetailsModel);
        expected.setSecretaryDetailsModel(appointmentDetailsModel);
        expected.setCompanyObjects(true);
        return expected;
    }

    private CertificateAppointmentDetailsModel getAppointmentDetails(String dobType) {
        CertificateAppointmentDetailsModel appointmentDetails = new CertificateAppointmentDetailsModel();
        appointmentDetails.setIncludeAddress(true);
        appointmentDetails.setIncludeAppointmentDate(true);
        appointmentDetails.setIncludeBasicInformation(true);
        appointmentDetails.setIncludeCountryOfResidence(true);
        appointmentDetails.setIncludeDobType(dobType);
        appointmentDetails.setIncludeNationality(true);
        appointmentDetails.setIncludeOccupation(true);
        return appointmentDetails;
    }
    private OrdersApi getOrder(DirectorOrSecretaryDetailsApi appointmentDetails) {
        OrdersApi order = new OrdersApi();
        order.setReference(TestConstants.ORDER_REFERENCE_NUMBER);

        CertificateApi item = new CertificateApi();
        item.setCompanyName(TestConstants.COMPANY_NAME);
        item.setCompanyNumber(TestConstants.COMPANY_NUMBER);

        CertificateItemOptionsApi itemOptions = new CertificateItemOptionsApi();

        CertificateTypeApi certificateType = CertificateTypeApi.INCORPORATION;
        itemOptions.setCertificateType(certificateType);
        itemOptions.setIncludeGoodStandingInformation(true);

        RegisteredOfficeAddressDetailsApi registeredOfficeAddressDetails = new RegisteredOfficeAddressDetailsApi();
        IncludeAddressRecordsTypeApi addressRecord = IncludeAddressRecordsTypeApi.CURRENT_PREVIOUS_AND_PRIOR;
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(addressRecord);
        registeredOfficeAddressDetails.setIncludeDates(true);

        itemOptions.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetails);
        itemOptions.setDirectorDetails(appointmentDetails);
        itemOptions.setSecretaryDetails(appointmentDetails);
        itemOptions.setIncludeCompanyObjectsInformation(true);
        item.setItemOptions(itemOptions);

        order.setItems(Collections.singletonList(item));
        order.setTotalOrderCost(TestConstants.ORDER_COST);
        order.setPaymentReference(TestConstants.PAYMENT_REFERENCE);
        order.setOrderedAt(LocalDateTime.of(2021, 7, 27, 15,20,10));

        return order;
    }

    private DirectorOrSecretaryDetailsApi getAppointmentApiDetails(IncludeDobTypeApi dobType) {
        DirectorOrSecretaryDetailsApi appointmentDetails = new DirectorOrSecretaryDetailsApi();
        appointmentDetails.setIncludeAddress(true);
        appointmentDetails.setIncludeAppointmentDate(true);
        appointmentDetails.setIncludeBasicInformation(true);
        appointmentDetails.setIncludeCountryOfResidence(true);
        appointmentDetails.setIncludeDobType(dobType);
        appointmentDetails.setIncludeNationality(true);
        appointmentDetails.setIncludeOccupation(true);
        return appointmentDetails;
    }

}
