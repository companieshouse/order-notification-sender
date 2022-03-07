package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.FeatureOptions;

@Component
public class LPCertificateOptionsMapper extends CertificateOptionsMapper {
    private final AddressRecordTypeMapper addressRecordTypeMapper;

    @Autowired
    public LPCertificateOptionsMapper(FeatureOptions featureOptions,
                                      CertificateTypeMapper certificateTypeMapper,
                                      AddressRecordTypeMapper addressRecordTypeMapper,
                                      DeliveryMethodMapper deliveryMethodMapper,
                                      OrdersApiDetailsCommonFieldsMapper commonFieldsMapper) {
        super(featureOptions, certificateTypeMapper, deliveryMethodMapper, commonFieldsMapper);
        this.addressRecordTypeMapper = addressRecordTypeMapper;
    }

    @Override
    protected void doMapCustomData(CertificateItemOptionsApi source, CertificateOrderNotificationModel destination) {
        destination.setPrincipalPlaceOfBusinessDetails(addressRecordTypeMapper.mapAddressRecordType(source.getPrincipalPlaceOfBusinessDetails().getIncludeAddressRecordsType()));
        destination.setGeneralPartnerDetails(MapUtil.mapBoolean(source.getGeneralPartnerDetails().getIncludeBasicInformation()));
        destination.setLimitedPartnerDetails(MapUtil.mapBoolean(source.getLimitedPartnerDetails().getIncludeBasicInformation()));
        destination.setGeneralNatureOfBusinessInformation(MapUtil.mapBoolean(source.getIncludeGeneralNatureOfBusinessInformation()));
        destination.setStatementOfGoodStanding(new Content<>(MapUtil.mapBoolean(source.getIncludeGoodStandingInformation())));
    }
}
