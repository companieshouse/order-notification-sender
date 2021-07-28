package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;

import java.util.Optional;

public class CertificateOrderNotificationMapper extends OrdersApiMapper {

    private final String messageId;
    private final String applicationId;
    private final String messageType;

    @Autowired
    public CertificateOrderNotificationMapper(DateGenerator dateGenerator, @Value("${email.date.format}") String dateFormat,
                                              @Value("${email.sender.address}") String senderEmail, @Value("${email.paymentDateFormat}") String paymentDateFormat,
                                              @Value("${email.certificate.messageId}") String messageId, @Value("${email.certificate.applicationId}") String applicationId,
                                              @Value("${email.certificate.messageType}") String messageType) {
        super(dateGenerator, dateFormat, paymentDateFormat, senderEmail);
        this.messageId = messageId;
        this.applicationId = applicationId;
        this.messageType = messageType;
    }

    @Override
    CertificateOrderNotificationModel generateEmailData(BaseItemApi item) {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi)item.getItemOptions();
        Optional.ofNullable(itemOptions.getCertificateType()).ifPresent(type -> model.setCertificateType(type.getJsonName()));
        model.setStatementOfGoodStanding(itemOptions.getIncludeGoodStandingInformation());

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
}
