package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

@Component
public class CertificateEmailDataMapper {

    private final CertificateTypeMapper certificateTypeMapper;
    private final DeliveryMethodMapper deliveryMethodMapper;

    public CertificateEmailDataMapper(CertificateTypeMapper certificateTypeMapper, DeliveryMethodMapper deliveryMethodMapper) {
        this.certificateTypeMapper = certificateTypeMapper;
        this.deliveryMethodMapper = deliveryMethodMapper;
    }

    Certificate map(BaseItemApi certificateItem) {
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) certificateItem.getItemOptions();
        return Certificate.builder()
                .withId(certificateItem.getId())
                .withCompanyNumber(certificateItem.getCompanyNumber())
                .withCertificateType(certificateTypeMapper.mapCertificateType(itemOptions.getCertificateType()))
                .withDeliveryMethod(deliveryMethodMapper.mapDeliveryMethod(
                        itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale()))
                .withFee("£" + certificateItem.getTotalItemCost())
                .build();
    }
}
