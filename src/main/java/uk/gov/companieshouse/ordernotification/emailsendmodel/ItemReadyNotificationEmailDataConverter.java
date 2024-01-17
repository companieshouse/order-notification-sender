package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.text.MessageFormat;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.ItemReadyEmailConfiguration;

public class ItemReadyNotificationEmailDataConverter extends OrderNotificationEmailDataConverter {

    private final ItemGroupProcessedSend itemReadyNotification;
    private final ItemReadyEmailConfiguration itemReadyEmailConfig;

    public ItemReadyNotificationEmailDataConverter(OrderNotificationEmailData emailData,
        CertificateEmailDataMapper certificateEmailDataMapper,
        CertifiedCopyEmailDataMapper certifiedCopyEmailDataMapper,
        MissingImageDeliveryEmailDataMapper missingImageDeliveryEmailDataMapper,
        EmailConfiguration emailConfiguration,
        ItemReadyEmailConfiguration itemReadyEmailConfig,
        ItemGroupProcessedSend itemReadyNotification) {
        super(emailData, certificateEmailDataMapper, certifiedCopyEmailDataMapper,
            missingImageDeliveryEmailDataMapper, emailConfiguration);
        this.itemReadyEmailConfig = itemReadyEmailConfig;
        this.itemReadyNotification = itemReadyNotification;
    }

    @Override
    public void mapOrder(OrdersApi ordersApi) {
        super.mapOrder(ordersApi);
        final ItemReadyNotificationEmailData emailData =
            (ItemReadyNotificationEmailData) getEmailData();
        emailData.setOrderNumber(itemReadyNotification.getOrderNumber());
        emailData.setGroupItem(itemReadyNotification.getGroupItem());
        emailData.setItemId(itemReadyNotification.getItem().getId());
        emailData.setDigitalDocumentLocation(
            itemReadyNotification.getItem().getDigitalDocumentLocation());
    }

    @Override
    protected String buildSubject(final OrdersApi ordersApi) {
        return MessageFormat.format(itemReadyEmailConfig.getSubject(),
            itemReadyNotification.getItem().getId(), ordersApi.getReference());
    }
}
