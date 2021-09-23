package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.ActionedByApi;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
class CertificateOrderNotificationMapperTest {

    private CertificateOrderNotificationMapper certificateOrderNotificationMapper;

    @Mock
    private CertificateTypeMapper certificateTypeMapper;

    @Mock
    private AddressRecordTypeMapper roaTypeMapper;

    @Mock
    private DeliveryMethodMapper deliveryMethodMapper;

    @Mock
    private DateGenerator dateGenerator;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private EmailConfiguration config;

    @Mock
    private EmailDataConfiguration emailDataConfig;

    @Mock
    private CertificateAppointmentDetailsMapper appointmentDetailsMapper;

    @Mock
    private CertificateAppointmentDetailsModel appointmentDetailsModel;

    @Mock
    private CertificateOptionsMapperFactory certificateOptionsMapperFactory;

    @InjectMocks
    private OtherCertificateOptionsMapper otherCertificateOptionsMapper;

    @BeforeEach
    void setup() {
        certificateOrderNotificationMapper = new CertificateOrderNotificationMapper(dateGenerator,
                config, new ObjectMapper(), certificateTypeMapper, roaTypeMapper, deliveryMethodMapper, appointmentDetailsMapper, certificateOptionsMapperFactory);
    }

    @Test
    void testCertificateOrderNotificationMapperMapsSuccessfully() throws JsonProcessingException {
        // given
        OrdersApi order = getOrder(getAppointmentApiDetails(IncludeDobTypeApi.FULL));
        when(dateGenerator.generate()).thenReturn(LocalDateTime.of(2021, 7, 27, 15, 20, 10));
        when(certificateTypeMapper.mapCertificateType(any())).thenReturn(TestConstants.CERTIFICATE_TYPE);
        when(roaTypeMapper.mapAddressRecordType(any())).thenReturn(TestConstants.EXPECTED_ADDRESS_TYPE);
        when(deliveryMethodMapper.mapDeliveryMethod(any(), any())).thenReturn(TestConstants.DELIVERY_METHOD);

        when(config.getDateFormat()).thenReturn(TestConstants.EMAIL_DATE_FORMAT);
        when(config.getSenderAddress()).thenReturn(TestConstants.SENDER_EMAIL_ADDRESS);
        when(config.getPaymentDateFormat()).thenReturn(TestConstants.PAYMENT_DATE_FORMAT);
        when(config.getApplicationId()).thenReturn(TestConstants.APPLICATION_ID);
        when(config.getConfirmationMessage()).thenReturn(TestConstants.CONFIRMATION_MESSAGE);
        when(config.getCertificate()).thenReturn(emailDataConfig);
        when(emailDataConfig.getMessageId()).thenReturn(TestConstants.MESSAGE_ID);
        when(emailDataConfig.getMessageType()).thenReturn(TestConstants.MESSAGE_TYPE);
        when(appointmentDetailsMapper.mapAppointmentDetails(any(DirectorOrSecretaryDetailsApi.class))).thenReturn(appointmentDetailsModel);
        when(appointmentDetailsModel.isSpecificDetails()).thenReturn(true);
        when(appointmentDetailsModel.getDetails()).thenReturn(Collections.singletonList(TestConstants.READABLE_TRUE));
        
        when(certificateOptionsMapperFactory.getCertificateOptionsMapper(any())).thenReturn(otherCertificateOptionsMapper);

        // when
        EmailSend result = certificateOrderNotificationMapper.map(order);

        // then
        assertEquals(getExpectedEmailSendModel(), result);
    }

    @Test
    void testCertificateOrderNotificationMapperReturnsMessageId() {
        //given
        when(config.getCertificate()).thenReturn(emailDataConfig);
        when(emailDataConfig.getMessageId()).thenReturn(TestConstants.MESSAGE_ID);

        //when
        String actual = certificateOrderNotificationMapper.getMessageId();

        //then
        assertEquals(TestConstants.MESSAGE_ID, actual);
    }

    @Test
    void testCertificateOrderNotificationMapperReturnsMessageType() {
        //given
        when(config.getCertificate()).thenReturn(emailDataConfig);
        when(emailDataConfig.getMessageType()).thenReturn(TestConstants.MESSAGE_TYPE);

        //when
        String actual = certificateOrderNotificationMapper.getMessageType();

        //then
        assertEquals(TestConstants.MESSAGE_TYPE, actual);
    }

    @Test
    void testMapperThrowsMappingExceptionIfJsonProcessingExceptionThrownByMapper() throws com.fasterxml.jackson.core.JsonProcessingException {
        //given
        when(config.getSenderAddress()).thenReturn(TestConstants.SENDER_EMAIL_ADDRESS);
        when(config.getPaymentDateFormat()).thenReturn(TestConstants.PAYMENT_DATE_FORMAT);
        when(config.getConfirmationMessage()).thenReturn(TestConstants.CONFIRMATION_MESSAGE);

        when(certificateOptionsMapperFactory.getCertificateOptionsMapper(any())).thenReturn(otherCertificateOptionsMapper);

        certificateOrderNotificationMapper = new CertificateOrderNotificationMapper(dateGenerator,
                config, mapper, certificateTypeMapper, roaTypeMapper, deliveryMethodMapper, appointmentDetailsMapper, certificateOptionsMapperFactory);
        OrdersApi order = getOrder(getAppointmentApiDetails(null));
        when(certificateTypeMapper.mapCertificateType(any())).thenReturn(TestConstants.CERTIFICATE_TYPE);
        when(roaTypeMapper.mapAddressRecordType(any())).thenReturn(TestConstants.EXPECTED_ADDRESS_TYPE);
        when(deliveryMethodMapper.mapDeliveryMethod(any(), any())).thenReturn(TestConstants.DELIVERY_METHOD);
        when(appointmentDetailsMapper.mapAppointmentDetails(any(DirectorOrSecretaryDetailsApi.class))).thenReturn(appointmentDetailsModel);
        when(mapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);

        //when
        Executable actual = () -> certificateOrderNotificationMapper.map(order);

        //then
        MappingException exception = assertThrows(MappingException.class, actual);
        assertEquals("Failed to map order: " + TestConstants.ORDER_REFERENCE_NUMBER, exception.getMessage());
    }

    private EmailSend getExpectedEmailSendModel() throws JsonProcessingException {
        EmailSend expected = new EmailSend();
        expected.setAppId(TestConstants.APPLICATION_ID);
        OrderModel model = getExpectedModel();
        model.setTo(TestConstants.EMAIL_RECIPIENT);
        model.setSubject(MessageFormat.format(TestConstants.CONFIRMATION_MESSAGE, TestConstants.ORDER_REFERENCE_NUMBER));
        model.setOrderReferenceNumber(TestConstants.ORDER_REFERENCE_NUMBER);
        model.setPaymentReference(TestConstants.PAYMENT_REFERENCE);
        model.setPaymentTime(TestConstants.PAYMENT_TIME);
        model.setAmountPaid(TestConstants.ORDER_VIEW);
        expected.setData(new ObjectMapper().writeValueAsString(model));
        expected.setCreatedAt(TestConstants.ORDER_CREATED_AT);
        expected.setEmailAddress(TestConstants.SENDER_EMAIL_ADDRESS);
        expected.setMessageId(TestConstants.MESSAGE_ID);
        expected.setMessageType(TestConstants.MESSAGE_TYPE);
        return expected;
    }

    private CertificateOrderNotificationModel getExpectedModel() {
        CertificateOrderNotificationModel expected = new CertificateOrderNotificationModel();
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

    private CertificateAppointmentDetailsModel getAppointmentDetails() {
        return new CertificateAppointmentDetailsModel(true, Collections.singletonList("Yes"));
    }
    private OrdersApi getOrder(DirectorOrSecretaryDetailsApi appointmentDetails) {
        OrdersApi order = new OrdersApi();
        ActionedByApi actionedByApi = new ActionedByApi();
        actionedByApi.setEmail(TestConstants.EMAIL_RECIPIENT);
        order.setReference(TestConstants.ORDER_REFERENCE_NUMBER);
        order.setOrderedBy(actionedByApi);

        CertificateApi item = new CertificateApi();
        item.setCompanyName(TestConstants.COMPANY_NAME);
        item.setCompanyNumber(TestConstants.COMPANY_NUMBER);

        CertificateItemOptionsApi itemOptions = new CertificateItemOptionsApi();

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
        appointmentDetails.setIncludeNationality(null);
        appointmentDetails.setIncludeOccupation(false);
        return appointmentDetails;
    }
}
