package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;

@Component
public class CertificateOrderMessageTypeFactory {
    private final EmailConfiguration emailConfiguration;

    public CertificateOrderMessageTypeFactory(EmailConfiguration emailConfiguration) {
        this.emailConfiguration = emailConfiguration;
    }

    public EmailDataConfiguration getMessageConfiguration(CertificateItemOptionsApi itemOptions) {
        if (itemOptions.getCertificateType() == CertificateTypeApi.DISSOLUTION) {
            return emailConfiguration.getDissolvedCertificate();
        } else {
            return emailConfiguration.getCertificate();
        }
    }
}
