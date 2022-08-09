package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.ordernotification.emailsender.NonRetryableFailureException;

@Component
public class SummaryEmailDataDirector {

    private final OrderNotificationDataConvertable converter;

    public SummaryEmailDataDirector(OrderNotificationDataConvertable converter) {
        this.converter = converter;
    }

    public OrderNotificationEmailData map(OrdersApi ordersApi) {
        converter.mapOrder(ordersApi);
        for (BaseItemApi itemApi: ordersApi.getItems()) {
            String kind = itemApi.getKind();
            if (Kind.CERTIFICATE.toString().equals(kind)) {
                converter.mapCertificate(itemApi);
            } else if (Kind.CERTIFIED_COPY.toString().equals(kind)) {
                converter.mapCertifiedCopy(itemApi);
            } else if (Kind.MISSING_IMAGE_DELIVERY.toString().equals(kind)) {
                converter.mapMissingImageDelivery(itemApi);
            } else {
                throw new NonRetryableFailureException(String.format("Unhandled kind: [%s]", kind));
            }
        }
        return converter.getEmailData();
    }
}
