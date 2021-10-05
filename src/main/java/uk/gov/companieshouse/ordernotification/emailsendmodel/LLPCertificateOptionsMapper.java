package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.FeatureOptions;

@Component
public class LLPCertificateOptionsMapper extends CertificateOptionsMapper {
    private final AddressRecordTypeMapper addressRecordTypeMapper;
    private final MembersDetailsApiMapper membersDetailsApiMapper;

    @Autowired
    public LLPCertificateOptionsMapper(FeatureOptions featureOptions,
                                       CertificateTypeMapper certificateTypeMapper,
                                       AddressRecordTypeMapper addressRecordTypeMapper,
                                       DeliveryMethodMapper deliveryMethodMapper,
                                       MembersDetailsApiMapper membersDetailsApiMapper) {
        super(featureOptions, certificateTypeMapper, deliveryMethodMapper);
        this.addressRecordTypeMapper = addressRecordTypeMapper;
        this.membersDetailsApiMapper = membersDetailsApiMapper;
    }

    @Override
    protected void doMapCustomData(CertificateItemOptionsApi source, CertificateOrderNotificationModel destination) {
        destination.setRegisteredOfficeAddressDetails(addressRecordTypeMapper.mapAddressRecordType(source.getRegisteredOfficeAddressDetails().getIncludeAddressRecordsType()));
        destination.setDesignatedMembersDetails(membersDetailsApiMapper.map(source.getDesignatedMemberDetails()));
        destination.setMembersDetails(membersDetailsApiMapper.map(source.getMemberDetails()));
    }
}
