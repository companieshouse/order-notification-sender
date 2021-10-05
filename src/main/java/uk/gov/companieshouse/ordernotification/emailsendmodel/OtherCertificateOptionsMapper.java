package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.FeatureOptions;

@Component
public class OtherCertificateOptionsMapper extends CertificateOptionsMapper {

    private final AddressRecordTypeMapper addressRecordTypeMapper;
    private final DirectorOrSecretaryDetailsApiMapper directorOrSecretaryDetailsApiMapper;

    @Autowired
    public OtherCertificateOptionsMapper(FeatureOptions featureOptions,
                                         CertificateTypeMapper certificateTypeMapper,
                                         AddressRecordTypeMapper addressRecordTypeMapper,
                                         DeliveryMethodMapper deliveryMethodMapper,
                                         DirectorOrSecretaryDetailsApiMapper directorOrSecretaryDetailsApiMapper) {
        super(featureOptions, certificateTypeMapper, deliveryMethodMapper);
        this.addressRecordTypeMapper = addressRecordTypeMapper;
        this.directorOrSecretaryDetailsApiMapper = directorOrSecretaryDetailsApiMapper;
    }

    @Override
    protected void doMapCustomData(CertificateItemOptionsApi source, CertificateOrderNotificationModel destination) {
        destination.setRegisteredOfficeAddressDetails(addressRecordTypeMapper.mapAddressRecordType(source.getRegisteredOfficeAddressDetails().getIncludeAddressRecordsType()));
        destination.setDirectorDetailsModel(directorOrSecretaryDetailsApiMapper.map(source.getDirectorDetails()));
        destination.setSecretaryDetailsModel(directorOrSecretaryDetailsApiMapper.map(source.getSecretaryDetails()));
        destination.setCompanyObjects(MapUtil.mapBoolean(source.getIncludeCompanyObjectsInformation()));
    }
}
