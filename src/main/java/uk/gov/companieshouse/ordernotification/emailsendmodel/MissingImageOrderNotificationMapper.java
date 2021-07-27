package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.BaseItemApi;

public class MissingImageOrderNotificationMapper extends OrdersApiMapper {

    public MissingImageOrderNotificationMapper(DateGenerator dateGenerator, String dateFormat, String paymentDateFormat, String senderEmail) {
        super(dateGenerator, dateFormat, paymentDateFormat, senderEmail);
    }

    @Override
    OrderModel generateEmailData(BaseItemApi order) {
        return null;
    }

    @Override
    String getMessageId() {
        return null;
    }

    @Override
    String getApplicationId() {
        return null;
    }

    @Override
    String getMessageType() {
        return null;
    }
}
