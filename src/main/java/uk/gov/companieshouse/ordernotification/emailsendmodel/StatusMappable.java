package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

public interface StatusMappable {
    void map(CertificateItemOptionsApi source, CertificateOrderNotificationModel target);
}
