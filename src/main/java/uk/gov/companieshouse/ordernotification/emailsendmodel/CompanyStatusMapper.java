package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static java.util.Objects.isNull;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

@Component
public class CompanyStatusMapper {

    public void map(CertificateItemOptionsApi source, CertificateOrderNotificationModel target) {
        if (CompanyStatus.LIQUIDATION == CompanyStatus.getEnumValue(source.getCompanyStatus())) {
            if (!isNull(source.getLiquidatorsDetails())) {
                target.setLiquidatorsDetails(MapUtil.mapBoolean(source.getLiquidatorsDetails()
                        .getIncludeBasicInformation()));
                target.setRenderLiquidatorsDetails(true);
            } else {
                target.setLiquidatorsDetails(null);
                target.setRenderLiquidatorsDetails(false);
            }
            target.setRenderStatementOfGoodStanding(false);
        } else {
            target.setStatementOfGoodStanding(MapUtil.mapBoolean(source.getIncludeGoodStandingInformation()));
            target.setRenderStatementOfGoodStanding(true);
            target.setRenderLiquidatorsDetails(false);
        }
    }
}