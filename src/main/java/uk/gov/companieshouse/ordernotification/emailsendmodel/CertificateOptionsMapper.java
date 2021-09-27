package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

public abstract class CertificateOptionsMapper {

    public static final String READABLE_FALSE = "No";
    public static final String READABLE_TRUE = "Yes";

    private final CertificateTypeMapper certificateTypeMapper;
    private final DeliveryMethodMapper deliveryMethodMapper;

    public CertificateOptionsMapper(CertificateTypeMapper certificateTypeMapper,
                                    DeliveryMethodMapper deliveryMethodMapper) {
        this.certificateTypeMapper = certificateTypeMapper;
        this.deliveryMethodMapper = deliveryMethodMapper;
    }

    public final CertificateOrderNotificationModel generateEmailData(BaseItemApi item) {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) item.getItemOptions();
        model.setCertificateType(getCertificateTypeMapper().mapCertificateType(itemOptions.getCertificateType()));
        model.setStatementOfGoodStanding(mapBoolean(itemOptions.getIncludeGoodStandingInformation()));
        model.setDeliveryMethod(getDeliveryMethodMapper().mapDeliveryMethod(itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale()));

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

    protected String mapBoolean(Boolean bool) {
        return booleanWrapperToBoolean(bool) ? READABLE_TRUE : READABLE_FALSE;
    }

    private boolean booleanWrapperToBoolean(Boolean bool) {
        return bool != null && bool;
    }
}
