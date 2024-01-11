package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

public class ItemReadyNotificationEmailDataConverter extends OrderNotificationEmailDataConverter {

    private final ItemGroupProcessedSend itemReadyNotification;

    public ItemReadyNotificationEmailDataConverter(OrderNotificationEmailData emailData,
        CertificateEmailDataMapper certificateEmailDataMapper,
        CertifiedCopyEmailDataMapper certifiedCopyEmailDataMapper,
        MissingImageDeliveryEmailDataMapper missingImageDeliveryEmailDataMapper,
        EmailConfiguration emailConfiguration,
        ItemGroupProcessedSend itemReadyNotification) {
        super(emailData, certificateEmailDataMapper, certifiedCopyEmailDataMapper,
            missingImageDeliveryEmailDataMapper, emailConfiguration);
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
        emailData.setDigitalDocumentLocation(itemReadyNotification.getItem().getDigitalDocumentLocation());
    }
}
