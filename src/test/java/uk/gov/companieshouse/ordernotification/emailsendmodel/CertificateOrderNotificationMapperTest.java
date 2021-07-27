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

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CertificateOrderNotificationMapperTest {

    private CertificateOrderNotificationMapper certificateOrderNotificationMapper;
    private final String ORDER_REFERENCE_NUMBER = "87654321";
    private final String COMPANY_NAME = "ACME LTD";
    private final String COMPANY_NUMBER = "12345678";
    private final String ORDER_COST = "15";
    private final String PAYMENT_REFERENCE = "ABCD-EFGH-IJKL";
    private final String CERTIFICATE_TYPE = "incorporation";
    private final String ADDRESS_TYPE = "current-previous-and-prior";
    private final String DOB_TYPE = "full";

    private final String MESSAGE_ID = "message_id";
    private final String APPLICATION_ID = "application_id";
    private final String MESSAGE_TYPE = "message_type";

    @Mock
    private DateGenerator dateGenerator;

    @BeforeEach
    void setup() {
        certificateOrderNotificationMapper = new CertificateOrderNotificationMapper(dateGenerator, "dd MMMM yyyy", "noreply@companieshouse.gov.uk", "dd MMMM yyyy - HH:mm:ss", MESSAGE_ID, APPLICATION_ID, MESSAGE_TYPE);
    }

    @Test
    void testCertificateOrderNotificationMapperMapsSuccessfully() throws JsonProcessingException {
        // given
        OrdersApi order = getOrder(getAppointmentApiDetails(IncludeDobTypeApi.FULL));
        when(dateGenerator.generate()).thenReturn(LocalDateTime.of(2021, 7, 27, 15, 20, 10));

        // when
        EmailSend result = certificateOrderNotificationMapper.map(order);

        // then
        assertEquals(getExpectedEmailSendModel(getAppointmentDetails(DOB_TYPE)), result);
    }

    @Test
    void testCertificateOrderNotificationMapperReturnsMessageId() {
        //when
        String actual = certificateOrderNotificationMapper.getMessageId();

        //then
        assertEquals(MESSAGE_ID, actual);
    }

    @Test
    void testCertificateOrderNotificationMapperReturnsApplicationId() {
        //when
        String actual = certificateOrderNotificationMapper.getApplicationId();

        //then
        assertEquals(APPLICATION_ID, actual);
    }

    @Test
    void testCertificateOrderNotificationMapperReturnsMessageType() {
        //when
        String actual = certificateOrderNotificationMapper.getMessageType();

        //then
        assertEquals(MESSAGE_TYPE, actual);
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
        expected.setAppId(APPLICATION_ID);
        OrderModel model = getExpectedModel(appointmentDetailsModel);
        model.setOrderReferenceNumber(ORDER_REFERENCE_NUMBER);
        model.setPaymentReference(PAYMENT_REFERENCE);
        model.setPaymentTime("27 July 2021 - 15:20:10");
        model.setTotalOrderCost(ORDER_COST);
        expected.setData(new ObjectMapper().writeValueAsString(model));
        expected.setCreatedAt("27 July 2021");
        expected.setEmailAddress("noreply@companieshouse.gov.uk");
        expected.setMessageId(MESSAGE_ID);
        expected.setMessageType(MESSAGE_TYPE);
        return expected;
    }

    private CertificateOrderNotificationModel getExpectedModel(CertificateAppointmentDetailsModel appointmentDetailsModel) {
        CertificateOrderNotificationModel expected = new CertificateOrderNotificationModel();
        expected.setCompanyName(COMPANY_NAME);
        expected.setCompanyNumber(COMPANY_NUMBER);
        expected.setCertificateType(CERTIFICATE_TYPE);
        expected.setStatementOfGoodStanding(true);
        expected.setCertificateRegisteredOfficeAddressModel(new CertificateRegisteredOfficeAddressModel(ADDRESS_TYPE, true));
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
        order.setReference(ORDER_REFERENCE_NUMBER);

        CertificateApi item = new CertificateApi();
        item.setCompanyName(COMPANY_NAME);
        item.setCompanyNumber(COMPANY_NUMBER);

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
        order.setTotalOrderCost(ORDER_COST);
        order.setPaymentReference(PAYMENT_REFERENCE);
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
