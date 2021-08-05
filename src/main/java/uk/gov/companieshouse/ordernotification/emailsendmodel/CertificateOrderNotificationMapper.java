package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class CertificateOrderNotificationMapper extends OrdersApiMapper {

    private final EmailConfiguration config;
    private final CertificateTypeMapper certificateTypeMapper;
    private final AddressRecordTypeMapper addressRecordTypeMapper;
    private final DeliveryMethodMapper deliveryMethodMapper;

    @Autowired
    public CertificateOrderNotificationMapper(DateGenerator dateGenerator, EmailConfiguration config,
                                              ObjectMapper mapper, CertificateTypeMapper certificateTypeMapper,
                                              AddressRecordTypeMapper addressRecordTypeMapper,
                                              DeliveryMethodMapper deliveryMethodMapper) {
        super(dateGenerator, config, mapper);
        this.config = config;
        this.certificateTypeMapper = certificateTypeMapper;
        this.addressRecordTypeMapper = addressRecordTypeMapper;
        this.deliveryMethodMapper = deliveryMethodMapper;
    }

    @Override
    CertificateOrderNotificationModel generateEmailData(BaseItemApi item) {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) item.getItemOptions();
        model.setCertificateType(certificateTypeMapper.mapCertificateType(itemOptions.getCertificateType()));
        model.setStatementOfGoodStanding(mapBoolean(itemOptions.getIncludeGoodStandingInformation()));
        model.setDeliveryMethod(deliveryMethodMapper.mapDeliveryMethod(itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale()));

        model.setRegisteredOfficeAddressDetails(addressRecordTypeMapper.mapAddressRecordType(itemOptions.getRegisteredOfficeAddressDetails().getIncludeAddressRecordsType()));

        model.setDirectorDetailsModel(mapAppointmentDetails(itemOptions.getDirectorDetails()));
        model.setSecretaryDetailsModel(mapAppointmentDetails(itemOptions.getSecretaryDetails()));

        model.setCompanyObjects(mapBoolean(itemOptions.getIncludeCompanyObjectsInformation()));
        return model;
    }

    private CertificateAppointmentDetailsModel mapAppointmentDetails(DirectorOrSecretaryDetailsApi appointment) {
        if(noAppointmentDetails(appointment)) {
            return new CertificateAppointmentDetailsModel(false, Collections.singletonList("No"));
        } else if(basicAppointmentDetails(appointment)) {
            return new CertificateAppointmentDetailsModel(false, Collections.singletonList("Yes"));
        } else {
            List<String> results = new ArrayList<>();
            if(booleanWrapperToBoolean(appointment.getIncludeAddress())){
                results.add("Correspondence address");
            }
            if(booleanWrapperToBoolean(appointment.getIncludeAppointmentDate())){
                results.add("Appointment date");
            }
            if(booleanWrapperToBoolean(appointment.getIncludeCountryOfResidence())){
                results.add("Country of residence");
            }
            if(booleanWrapperToBoolean(appointment.getIncludeNationality())){
                results.add("Nationality");
            }
            if(booleanWrapperToBoolean(appointment.getIncludeOccupation())){
                results.add("Occupation");
            }
            if(appointment.getIncludeDobType() != null) {
                results.add("Date of birth (month and year)");
            }
            return new CertificateAppointmentDetailsModel(true, results);
        }
    }

    private boolean noAppointmentDetails(DirectorOrSecretaryDetailsApi appointment) {
        return !booleanWrapperToBoolean(appointment.getIncludeBasicInformation());
    }

    private boolean basicAppointmentDetails(DirectorOrSecretaryDetailsApi appointment) {
        return booleanWrapperToBoolean(appointment.getIncludeBasicInformation()) &&
                !booleanWrapperToBoolean(appointment.getIncludeAddress()) &&
                !booleanWrapperToBoolean(appointment.getIncludeAppointmentDate()) &&
                !booleanWrapperToBoolean(appointment.getIncludeCountryOfResidence()) &&
                !booleanWrapperToBoolean(appointment.getIncludeNationality()) &&
                !booleanWrapperToBoolean(appointment.getIncludeOccupation()) &&
                appointment.getIncludeDobType() == null;
    }

    private String mapBoolean(Boolean bool) {
        return booleanWrapperToBoolean(bool) ? "Yes" : "No";
    }

    private boolean booleanWrapperToBoolean(Boolean bool) {
        return bool != null && bool;
    }

    @Override
    String getMessageId() {
        return config.getCertificate().getMessageId();
    }

    @Override
    String getMessageType() {
        return config.getCertificate().getMessageType();
    }
}
