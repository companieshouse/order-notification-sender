package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;

@Component
public class CertificateEmailDataMapper {

    private final CertificateTypeMapper certificateTypeMapper;
    private final Map<DeliveryTimescaleApi, String> deliveryMappings;

    public CertificateEmailDataMapper(CertificateTypeMapper certificateTypeMapper,
                                      @Qualifier("deliveryMethodMappings") Map<DeliveryTimescaleApi, String> deliveryMappings) {
        this.certificateTypeMapper = certificateTypeMapper;
        this.deliveryMappings = deliveryMappings;
    }

    Certificate map(BaseItemApi certificateItem) {
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) certificateItem.getItemOptions();
        return Certificate.builder()
                .withId(certificateItem.getId())
                .withCompanyNumber(certificateItem.getCompanyNumber())
                .withCertificateType(certificateTypeMapper.mapCertificateType(itemOptions.getCertificateType()))
                .withDeliveryMethod(deliveryMappings.get(itemOptions.getDeliveryTimescale()))
                .withFee("£" + certificateItem.getTotalItemCost())
                .build();
    }
}
