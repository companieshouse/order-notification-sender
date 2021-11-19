package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.LiquidatorsDetailsApi;

import java.util.Optional;

@Component
public class LiquidatorsDetailsApiMapper {

    public void map(CertificateItemOptionsApi source, CertificateOrderNotificationModel target) {
        target.setLiquidatorsDetails(
                Optional.ofNullable(source.getLiquidatorsDetails())
                        .map(LiquidatorsDetailsApi::getIncludeBasicInformation)
                        .map(MapUtil::mapBoolean).orElse(null));
    }
}
