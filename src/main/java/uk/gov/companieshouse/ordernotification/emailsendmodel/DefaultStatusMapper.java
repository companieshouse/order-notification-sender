package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

@Component
public class DefaultStatusMapper implements StatusMappable {

    @Override
    public void map(CertificateItemOptionsApi source, CertificateOrderNotificationModel target) {
        target.setStatementOfGoodStanding(new Content<>(MapUtil.mapBoolean(source.getIncludeGoodStandingInformation())));
    }
}
