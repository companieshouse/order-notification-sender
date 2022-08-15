package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.ordernotification.emailsender.NonRetryableFailureException;

import java.util.Objects;

public class SummaryEmailDataDirector {

    private final OrderNotificationDataConvertable converter;

    public SummaryEmailDataDirector(OrderNotificationDataConvertable converter) {
        this.converter = converter;
    }

    public void map(OrdersApi ordersApi) {
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SummaryEmailDataDirector that = (SummaryEmailDataDirector) o;
        return Objects.equals(converter, that.converter);
    }

    @Override
    public int hashCode() {
        return Objects.hash(converter);
    }
}
