package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;

import java.util.Optional;

@Component
public class CertificateOrderNotificationMapper extends OrdersApiMapper {

    private final String messageId;
    private final String applicationId;
    private final String messageType;
    private final String confirmationMessage;
    private final CertificateTypeMapper certificateTypeMapper;

    @Autowired
    public CertificateOrderNotificationMapper(DateGenerator dateGenerator, @Value("${email.dateFormat}") String dateFormat,
                                              @Value("${email.senderAddress}") String senderEmail, @Value("${email.paymentDateFormat}") String paymentDateFormat,
                                              @Value("${email.certificate.messageId}") String messageId, @Value("${email.applicationId}") String applicationId,
                                              @Value("${email.certificate.messageType}") String messageType, @Value("${email.confirmationMessage}") String confirmationMessage,
                                              ObjectMapper mapper, CertificateTypeMapper certificateTypeMapper) {
        super(dateGenerator, dateFormat, paymentDateFormat, senderEmail, mapper);
        this.messageId = messageId;
        this.applicationId = applicationId;
        this.messageType = messageType;
        this.confirmationMessage = confirmationMessage;
        this.certificateTypeMapper = certificateTypeMapper;
    }

    @Override
    CertificateOrderNotificationModel generateEmailData(BaseItemApi item) {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) item.getItemOptions();
        model.setCertificateType(certificateTypeMapper.mapCertificateType(itemOptions.getCertificateType()));
        model.setStatementOfGoodStanding(itemOptions.getIncludeGoodStandingInformation());
        Optional.ofNullable(itemOptions.getDeliveryMethod()).ifPresent(method -> model.setDeliveryMethod(method.getJsonName()));

        CertificateRegisteredOfficeAddressModel certificateRegisteredOfficeAddressModel =
                new CertificateRegisteredOfficeAddressModel(Optional.ofNullable(itemOptions.getRegisteredOfficeAddressDetails().getIncludeAddressRecordsType()).map(IncludeAddressRecordsTypeApi::getJsonName).orElse(null),
                        itemOptions.getRegisteredOfficeAddressDetails().getIncludeDates());
        model.setCertificateRegisteredOfficeAddressModel(certificateRegisteredOfficeAddressModel);

        model.setDirectorDetailsModel(mapAppointmentDetails(itemOptions.getDirectorDetails()));
        model.setSecretaryDetailsModel(mapAppointmentDetails(itemOptions.getSecretaryDetails()));

        model.setCompanyObjects(itemOptions.getIncludeCompanyObjectsInformation());
        return model;
    }

    private CertificateAppointmentDetailsModel mapAppointmentDetails(DirectorOrSecretaryDetailsApi appointment) {
        CertificateAppointmentDetailsModel result = new CertificateAppointmentDetailsModel();
        result.setIncludeAddress(appointment.getIncludeAddress());
        result.setIncludeAppointmentDate(appointment.getIncludeAppointmentDate());
        result.setIncludeBasicInformation(appointment.getIncludeBasicInformation());
        result.setIncludeCountryOfResidence(appointment.getIncludeCountryOfResidence());
        Optional.ofNullable(appointment.getIncludeDobType()).ifPresent(dob -> result.setIncludeDobType(dob.getJsonName()));
        result.setIncludeNationality(appointment.getIncludeNationality());
        result.setIncludeOccupation(appointment.getIncludeOccupation());
        return result;
    }

    @Override
    String getMessageId() {
        return messageId;
    }

    @Override
    String getApplicationId() {
        return applicationId;
    }

    @Override
    String getMessageType() {
        return messageType;
    }

    @Override
    String getMessageSubject() {
        return confirmationMessage;
    }
}
