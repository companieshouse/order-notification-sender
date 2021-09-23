package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@Component
public class CertificateOrderNotificationMapper extends OrdersApiMapper {

    private final EmailConfiguration config;
    private final CertificateTypeMapper certificateTypeMapper;
    private final AddressRecordTypeMapper addressRecordTypeMapper;
    private final DeliveryMethodMapper deliveryMethodMapper;
    private final CertificateAppointmentDetailsMapper appointmentDetailsMapper;
    private final CertificateOptionsMapperFactory certificateOptionsMapperFactory;

    @Autowired
    public CertificateOrderNotificationMapper(DateGenerator dateGenerator, EmailConfiguration config,
                                              ObjectMapper mapper, CertificateTypeMapper certificateTypeMapper,
                                              AddressRecordTypeMapper addressRecordTypeMapper,
                                              DeliveryMethodMapper deliveryMethodMapper,
                                              CertificateAppointmentDetailsMapper appointmentDetailsMapper,
                                              CertificateOptionsMapperFactory certificateOptionsMapperFactory) {
        super(dateGenerator, config, mapper);
        this.config = config;
        this.certificateTypeMapper = certificateTypeMapper;
        this.addressRecordTypeMapper = addressRecordTypeMapper;
        this.deliveryMethodMapper = deliveryMethodMapper;
        this.appointmentDetailsMapper = appointmentDetailsMapper;
        this.certificateOptionsMapperFactory = certificateOptionsMapperFactory;
    }

    @Override
    CertificateOrderNotificationModel generateEmailData(BaseItemApi item) {
        return certificateOptionsMapperFactory.getCertificateOptionsMapper(
                ((CertificateItemOptionsApi) item.getItemOptions()).getCompanyType()).generateEmailData(item);
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
