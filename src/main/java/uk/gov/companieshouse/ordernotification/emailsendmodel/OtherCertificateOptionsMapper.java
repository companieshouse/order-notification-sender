package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@Component
public class OtherCertificateOptionsMapper extends CertificateOptionsMapper {

    private CertificateAppointmentDetailsMapper appointmentDetailsMapper;

    @Autowired
    public OtherCertificateOptionsMapper(EmailConfiguration config,
                                         CertificateTypeMapper certificateTypeMapper,
                                         AddressRecordTypeMapper addressRecordTypeMapper,
                                         DeliveryMethodMapper deliveryMethodMapper,
                                         CertificateAppointmentDetailsMapper appointmentDetailsMapper) {
        super(config, certificateTypeMapper, addressRecordTypeMapper, deliveryMethodMapper);
        this.appointmentDetailsMapper = appointmentDetailsMapper;
    }

    @Override
    public CertificateOrderNotificationModel generateEmailData(BaseItemApi item) {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) item.getItemOptions();
        model.setCertificateType(getCertificateTypeMapper().mapCertificateType(itemOptions.getCertificateType()));
        model.setStatementOfGoodStanding(mapBoolean(itemOptions.getIncludeGoodStandingInformation()));
        model.setDeliveryMethod(getDeliveryMethodMapper().mapDeliveryMethod(itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale()));

        model.setRegisteredOfficeAddressDetails(getAddressRecordTypeMapper().mapAddressRecordType(itemOptions.getRegisteredOfficeAddressDetails().getIncludeAddressRecordsType()));

        model.setDirectorDetailsModel(getAppointmentDetailsMapper().mapAppointmentDetails(itemOptions.getDirectorDetails()));
        model.setSecretaryDetailsModel(getAppointmentDetailsMapper().mapAppointmentDetails(itemOptions.getSecretaryDetails()));

        model.setCompanyObjects(mapBoolean(itemOptions.getIncludeCompanyObjectsInformation()));
        return model;
    }

    protected CertificateAppointmentDetailsMapper getAppointmentDetailsMapper() {
        return appointmentDetailsMapper;
    }
}
