package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.FeatureOptions;

@Component
public class OtherCertificateOptionsMapper extends CertificateOptionsMapper {

    private final AddressRecordTypeMapper addressRecordTypeMapper;
    private final DirectorOrSecretaryDetailsApiMapper directorOrSecretaryDetailsApiMapper;
    private final LiquidatorsDetailsApiMapper liquidatorsDetailsApiMapper;

    @Autowired
    public OtherCertificateOptionsMapper(FeatureOptions featureOptions,
                                         CertificateTypeMapper certificateTypeMapper,
                                         AddressRecordTypeMapper addressRecordTypeMapper,
                                         DeliveryMethodMapper deliveryMethodMapper,
                                         DirectorOrSecretaryDetailsApiMapper directorOrSecretaryDetailsApiMapper,
                                         LiquidatorsDetailsApiMapper liquidatorsDetailsApiMapper) {
        super(featureOptions, certificateTypeMapper, deliveryMethodMapper);
        this.addressRecordTypeMapper = addressRecordTypeMapper;
        this.directorOrSecretaryDetailsApiMapper = directorOrSecretaryDetailsApiMapper;
        this.liquidatorsDetailsApiMapper = liquidatorsDetailsApiMapper;
    }

    @Override
    protected void doMapCustomData(CertificateItemOptionsApi source, CertificateOrderNotificationModel destination) {
        destination.setRegisteredOfficeAddressDetails(addressRecordTypeMapper.mapAddressRecordType(source.getRegisteredOfficeAddressDetails().getIncludeAddressRecordsType()));
        destination.setDirectorDetailsModel(directorOrSecretaryDetailsApiMapper.map(source.getDirectorDetails()));
        destination.setSecretaryDetailsModel(directorOrSecretaryDetailsApiMapper.map(source.getSecretaryDetails()));
        destination.setCompanyObjects(MapUtil.mapBoolean(source.getIncludeCompanyObjectsInformation()));
        liquidatorsDetailsApiMapper.map(source, destination);
    }
}
