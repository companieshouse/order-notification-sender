package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.FeatureOptions;

public abstract class CertificateOptionsMapper {

    private final FeatureOptions featureOptions;
    private final CertificateTypeMapper certificateTypeMapper;
    private final DeliveryMethodMapper deliveryMethodMapper;

    protected CertificateOptionsMapper(FeatureOptions featureOptions, CertificateTypeMapper certificateTypeMapper,
                                    DeliveryMethodMapper deliveryMethodMapper) {
        this.featureOptions = featureOptions;
        this.certificateTypeMapper = certificateTypeMapper;
        this.deliveryMethodMapper = deliveryMethodMapper;
    }

    public final CertificateOrderNotificationModel generateEmailData(BaseItemApi item) {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) item.getItemOptions();
        model.setCompanyType(itemOptions.getCompanyType());
        model.setCompanyName(item.getCompanyName());
        model.setCompanyNumber(item.getCompanyNumber());
        model.setCertificateType(getCertificateTypeMapper().mapCertificateType(itemOptions.getCertificateType()));
        model.setDeliveryMethod(getDeliveryMethodMapper().mapDeliveryMethod(itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale()));
        model.setFeatureOptions(featureOptions);

        doMapCustomData(itemOptions, model);

        return model;
    }

    protected abstract void doMapCustomData(CertificateItemOptionsApi source, CertificateOrderNotificationModel destination);

    protected CertificateTypeMapper getCertificateTypeMapper() {
        return certificateTypeMapper;
    }

    protected DeliveryMethodMapper getDeliveryMethodMapper() {
        return deliveryMethodMapper;
    }
}