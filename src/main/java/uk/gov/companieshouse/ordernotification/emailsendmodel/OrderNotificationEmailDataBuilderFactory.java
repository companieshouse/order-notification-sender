package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.ItemReadyEmailConfiguration;

@Component
public class OrderNotificationEmailDataBuilderFactory {

    private final CertificateEmailDataMapper certificateEmailDataMapper;
    private final CertifiedCopyEmailDataMapper certifiedCopyEmailDataMapper;
    private final MissingImageDeliveryEmailDataMapper missingImageDeliveryEmailDataMapper;
    private final EmailConfiguration emailConfiguration;

    public OrderNotificationEmailDataBuilderFactory(CertificateEmailDataMapper certificateEmailDataMapper,
                                                    CertifiedCopyEmailDataMapper certifiedCopyEmailDataMapper,
                                                    MissingImageDeliveryEmailDataMapper missingImageDeliveryEmailDataMapper,
                                                    EmailConfiguration emailConfiguration) {
        this.certificateEmailDataMapper = certificateEmailDataMapper;
        this.certifiedCopyEmailDataMapper = certifiedCopyEmailDataMapper;
        this.missingImageDeliveryEmailDataMapper = missingImageDeliveryEmailDataMapper;
        this.emailConfiguration = emailConfiguration;
    }

    OrderNotificationDataConvertable newConverter() {
        return new OrderNotificationEmailDataConverter(
                new OrderNotificationEmailData(),
                this.certificateEmailDataMapper,
                this.certifiedCopyEmailDataMapper,
                this.missingImageDeliveryEmailDataMapper,
                this.emailConfiguration
        );
    }

    OrderNotificationDataConvertable newConverter(final ItemGroupProcessedSend itemReadyNotification,
        final ItemReadyEmailConfiguration itemReadyEmailConfig) {
        return new ItemReadyNotificationEmailDataConverter(
            new ItemReadyNotificationEmailData(),
            this.certificateEmailDataMapper,
            this.certifiedCopyEmailDataMapper,
            this.missingImageDeliveryEmailDataMapper,
            this.emailConfiguration,
            itemReadyEmailConfig,
            itemReadyNotification
        );
    }

    SummaryEmailDataDirector newDirector(OrderNotificationDataConvertable convertable) {
        return new SummaryEmailDataDirector(convertable);
    }
}
