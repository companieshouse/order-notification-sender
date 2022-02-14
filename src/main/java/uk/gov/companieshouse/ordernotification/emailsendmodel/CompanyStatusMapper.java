package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static java.util.Objects.isNull;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

@Component
public class CompanyStatusMapper {

    public void map(CertificateItemOptionsApi source, CertificateOrderNotificationModel target) {
        if (CompanyStatus.LIQUIDATION == CompanyStatus.getEnumValue(source.getCompanyStatus())) {
            if (!isNull(source.getLiquidatorsDetails())) {
                target.setLiquidatorsDetails(new Content<>(MapUtil.mapBoolean(source.getLiquidatorsDetails()
                        .getIncludeBasicInformation())));
            } else {
                target.setLiquidatorsDetails(null);
            }
            target.setStatementOfGoodStanding(null);
        } else {
            target.setLiquidatorsDetails(null);
            target.setStatementOfGoodStanding(new Content<>(MapUtil.mapBoolean(source.getIncludeGoodStandingInformation())));
        }
    }
}