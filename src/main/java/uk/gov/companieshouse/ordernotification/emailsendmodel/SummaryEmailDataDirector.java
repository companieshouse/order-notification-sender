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
            if ("item#certificate".equals(kind)) {
                converter.mapCertificate(itemApi);
            } else if ("item#certified-copy".equals(kind)) {
                converter.mapCertifiedCopy(itemApi);
            } else if ("item#missing-image-delivery".equals(kind)) {
                converter.mapMissingImageDelivery(itemApi);
            } else {
                throw new NonRetryableFailureException(String.format("Unhandled kind: [%s]", kind));
            }
        }
        return converter.getEmailData();
    }
}
