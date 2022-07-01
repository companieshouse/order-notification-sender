package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.FeatureOptions;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

public abstract class CertificateOptionsMapper {

    private final FeatureOptions featureOptions;
    private final CertificateTypeMapper certificateTypeMapper;
    private final DeliveryMethodMapper deliveryMethodMapper;
    private final OrdersApiDetailsCommonFieldsMapper commonFieldsMapper;
    private final String COMPANY_STATUS_DISSOLVED = "dissolved";

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
        model.setCompanyStatus(itemOptions.getCompanyStatus());
        model.setCompanyName(ordersApiDetails.getCompanyName());
        model.setCompanyNumber(ordersApiDetails.getCompanyNumber());
        model.setCertificateType(getCertificateTypeMapper().mapCertificateType(itemOptions.getCertificateType()));
        model.setDeliveryMethod(getDeliveryMethodMapper().mapDeliveryMethod(itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale()));
        model.setFeatureOptions(featureOptions);
        commonFieldsMapper.mapCommonFields(model, ordersApiDetails);

        if (!COMPANY_STATUS_DISSOLVED.equals(itemOptions.getCompanyStatus())) {
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