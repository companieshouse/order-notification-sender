package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@Component
public class LPCertificateOptionsMapper extends CertificateOptionsMapper {

    @Autowired
    public LPCertificateOptionsMapper(EmailConfiguration config,
                                      CertificateTypeMapper certificateTypeMapper,
                                      AddressRecordTypeMapper addressRecordTypeMapper,
                                      DeliveryMethodMapper deliveryMethodMapper) {
        super(config, certificateTypeMapper, addressRecordTypeMapper, deliveryMethodMapper);
    }

    @Override
    public CertificateOrderNotificationModel generateEmailData(BaseItemApi item) {
        return null;
    }
}
