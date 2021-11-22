package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

import java.util.Optional;

@Component
public class LiquidatorsDetailsApiMapper {

    public void map(CertificateItemOptionsApi source, CertificateOrderNotificationModel target) {
        target.setLiquidatorsDetails(
                Optional.ofNullable(source.getLiquidatorsDetails())
                        .map(liquidatorsDetails -> Optional.ofNullable(liquidatorsDetails.getIncludeBasicInformation())
                                .orElse(Boolean.FALSE))
                        .map(MapUtil::mapBoolean).orElse(null));
    }
}
