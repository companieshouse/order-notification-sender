package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;
import uk.gov.companieshouse.ordernotification.config.FeatureOptions;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

public abstract class CertificateOptionsMapper {

    private final FeatureOptions featureOptions;
    private final CertificateTypeMapper certificateTypeMapper;
    private final DeliveryMethodMapper deliveryMethodMapper;
    private final OrdersApiDetailsCommonFieldsMapper commonFieldsMapper;

    protected CertificateOptionsMapper(FeatureOptions featureOptions,
                                       CertificateTypeMapper certificateTypeMapper,
                                       DeliveryMethodMapper deliveryMethodMapper,
                                       OrdersApiDetailsCommonFieldsMapper commonFieldsMapper) {
        this.featureOptions = featureOptions;
        this.certificateTypeMapper = certificateTypeMapper;
        this.deliveryMethodMapper = deliveryMethodMapper;
        this.commonFieldsMapper = commonFieldsMapper;
    }

    public final CertificateOrderNotificationModel generateEmailData(OrdersApiDetails ordersApiDetails) {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) ordersApiDetails.getItemOptions();
        model.setCompanyType(itemOptions.getCompanyType());
        model.setCompanyName(ordersApiDetails.getCompanyName());
        model.setCompanyNumber(ordersApiDetails.getCompanyNumber());
        model.setCertificateType(getCertificateTypeMapper().mapCertificateType(itemOptions.getCertificateType()));
        model.setDeliveryMethod(getDeliveryMethodMapper().mapDeliveryMethod(itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale()));
        model.setDeliveryTimescale(itemOptions.getDeliveryTimescale().toString());
        model.setEmailCopyRequired(EmailRequiredMapper.mapIsEmailRequired(itemOptions));
        model.setFeatureOptions(featureOptions);
        commonFieldsMapper.mapCommonFields(model, ordersApiDetails);

        if (itemOptions.getCertificateType() != CertificateTypeApi.DISSOLUTION) {
            doMapCustomData(itemOptions, model);
        }

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