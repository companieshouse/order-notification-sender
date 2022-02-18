package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

import static java.util.Objects.isNull;

@Component
public class LiquidationStatusMapper implements StatusMappable {

    @Override
    public void map(CertificateItemOptionsApi source, CertificateOrderNotificationModel target) {
        if (!isNull(source.getLiquidatorsDetails())) {
            target.setLiquidatorsDetails(new Content<>(MapUtil.mapBoolean(source.getLiquidatorsDetails().getIncludeBasicInformation())));
        }
    }
}
