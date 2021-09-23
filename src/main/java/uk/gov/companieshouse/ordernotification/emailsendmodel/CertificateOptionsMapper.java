package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

public abstract class CertificateOptionsMapper {

    public static final String READABLE_FALSE = "No";
    public static final String READABLE_TRUE = "Yes";

    private final EmailConfiguration config;
    private final CertificateTypeMapper certificateTypeMapper;
    private final AddressRecordTypeMapper addressRecordTypeMapper;
    private final DeliveryMethodMapper deliveryMethodMapper;

    public CertificateOptionsMapper(EmailConfiguration config,
                                    CertificateTypeMapper certificateTypeMapper,
                                    AddressRecordTypeMapper addressRecordTypeMapper,
                                    DeliveryMethodMapper deliveryMethodMapper) {
        this.config = config;
        this.certificateTypeMapper = certificateTypeMapper;
        this.addressRecordTypeMapper = addressRecordTypeMapper;
        this.deliveryMethodMapper = deliveryMethodMapper;
    }

    public abstract CertificateOrderNotificationModel generateEmailData(BaseItemApi item);

    protected EmailConfiguration getConfig() {
        return config;
    }

    protected CertificateTypeMapper getCertificateTypeMapper() {
        return certificateTypeMapper;
    }

    protected AddressRecordTypeMapper getAddressRecordTypeMapper() {
        return addressRecordTypeMapper;
    }

    protected DeliveryMethodMapper getDeliveryMethodMapper() {
        return deliveryMethodMapper;
    }

    protected String mapBoolean(Boolean bool) {
        return booleanWrapperToBoolean(bool) ? READABLE_TRUE : READABLE_FALSE;
    }

    private boolean booleanWrapperToBoolean(Boolean bool) {
        return bool != null && bool;
    }

}
