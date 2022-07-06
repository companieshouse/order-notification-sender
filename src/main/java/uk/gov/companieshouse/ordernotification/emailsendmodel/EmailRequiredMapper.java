package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;

public class EmailRequiredMapper {

    static String mapIsEmailRequired(CertificateItemOptionsApi item) {
        if (item.getDeliveryTimescale().toString().equals(DeliveryTimescaleApi.SAME_DAY.toString())) {
            if (item.getIncludeEmailCopy()) {
                return "Yes";
            } else {
                return "No";
            }
        } else {
            return "Email only available for express delivery method";
        }
    }
}
