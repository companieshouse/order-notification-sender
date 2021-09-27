package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

@Component
public class LPCertificateOptionsMapper extends CertificateOptionsMapper {
    private final AddressRecordTypeMapper addressRecordTypeMapper;

    @Autowired
    public LPCertificateOptionsMapper(CertificateTypeMapper certificateTypeMapper,
                                      AddressRecordTypeMapper addressRecordTypeMapper,
                                      DeliveryMethodMapper deliveryMethodMapper) {
        super(certificateTypeMapper, deliveryMethodMapper);
        this.addressRecordTypeMapper = addressRecordTypeMapper;
    }

    @Override
    protected void doMapCustomData(CertificateItemOptionsApi source, CertificateOrderNotificationModel destination) {
        destination.setPrincipalPlaceOfBusinessDetails(addressRecordTypeMapper.mapAddressRecordType(source.getPrincipalPlaceOfBusinessDetails().getIncludeAddressRecordsType()));
        destination.setGeneralPartnerDetails(mapBoolean(source.getGeneralPartnerDetails().getIncludeBasicInformation()));
        destination.setLimitedPartnerDetails(mapBoolean(source.getLimitedPartnerDetails().getIncludeBasicInformation()));
        destination.setGeneralNatureOfBusinessInformation(mapBoolean(source.getIncludeGeneralNatureOfBusinessInformation()));
    }
}
