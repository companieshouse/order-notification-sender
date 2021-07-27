package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.CertificateApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

public class CertificateOrderNotificationMapper extends OrdersApiMapper {

    @Autowired
    public CertificateOrderNotificationMapper(DateGenerator dateGenerator, @Value("${email.date.format}") String dateFormat, @Value("${email.sender.address}") String senderEmail) {
        super(dateGenerator, dateFormat, senderEmail);
    }

    @Override
    CertificateOrderNotificationModel generateEmailData(OrdersApi order) {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateApi item = (CertificateApi)order.getItems().get(0); //TODO: for each item
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi)item.getItemOptions();
        model.setOrderReferenceNumber(model.getOrderReferenceNumber());
        model.setCompanyName(item.getCompanyName());
        model.setCompanyNumber(item.getCompanyNumber());
        model.setCertificateType(itemOptions.getCertificateType().getJsonName());
        model.setStatementOfGoodStanding(itemOptions.getIncludeGoodStandingInformation());

        CertificateRegisteredOfficeAddressModel certificateRegisteredOfficeAddressModel =
                new CertificateRegisteredOfficeAddressModel(itemOptions.getRegisteredOfficeAddressDetails().getIncludeAddressRecordsType().getJsonName(),
                        itemOptions.getRegisteredOfficeAddressDetails().getIncludeDates());
        model.setCertificateRegisteredOfficeAddressModel(certificateRegisteredOfficeAddressModel);

        CertificateAppointmentDetailsModel directorDetailsModel = new CertificateAppointmentDetailsModel();
        directorDetailsModel.setIncludeAddress(itemOptions.getDirectorDetails().getIncludeAddress());
        directorDetailsModel.setIncludeAppointmentDate(itemOptions.getDirectorDetails().getIncludeAppointmentDate());
        directorDetailsModel.setIncludeBasicInformation(itemOptions.getDirectorDetails().getIncludeBasicInformation());
        directorDetailsModel.setIncludeCountryOfResidence(itemOptions.getDirectorDetails().getIncludeCountryOfResidence());
        directorDetailsModel.setIncludeDobType(itemOptions.getDirectorDetails().getIncludeDobType().getJsonName());
        directorDetailsModel.setIncludeNationality(itemOptions.getDirectorDetails().getIncludeNationality());
        directorDetailsModel.setIncludeOccupation(itemOptions.getDirectorDetails().getIncludeOccupation());
        model.setDirectorDetailsModel(directorDetailsModel);

        CertificateAppointmentDetailsModel secretaryDetailsModel = new CertificateAppointmentDetailsModel();
        secretaryDetailsModel.setIncludeAddress(itemOptions.getSecretaryDetails().getIncludeAddress());
        secretaryDetailsModel.setIncludeAppointmentDate(itemOptions.getSecretaryDetails().getIncludeAppointmentDate());
        secretaryDetailsModel.setIncludeBasicInformation(itemOptions.getSecretaryDetails().getIncludeBasicInformation());
        secretaryDetailsModel.setIncludeCountryOfResidence(itemOptions.getSecretaryDetails().getIncludeCountryOfResidence());
        secretaryDetailsModel.setIncludeDobType(itemOptions.getSecretaryDetails().getIncludeDobType().getJsonName());
        secretaryDetailsModel.setIncludeNationality(itemOptions.getSecretaryDetails().getIncludeNationality());
        secretaryDetailsModel.setIncludeOccupation(itemOptions.getSecretaryDetails().getIncludeOccupation());
        model.setDirectorDetailsModel(secretaryDetailsModel);

        model.setCompanyObjects(itemOptions.getIncludeCompanyObjectsInformation());
        model.setAmountPaid(order.getTotalOrderCost());
        model.setPaymentReference(order.getPaymentReference());
        return model;
    }

    @Override
    String getMessageId() {
        return null;
    }

    @Override
    String getApplicationId() {
        return null;
    }

    @Override
    String getMessageType() {
        return null;
    }
}
