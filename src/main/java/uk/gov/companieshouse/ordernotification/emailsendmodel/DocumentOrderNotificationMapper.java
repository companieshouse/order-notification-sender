package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.OrdersApi;

public class DocumentOrderNotificationMapper extends OrdersApiMapper {

    public DocumentOrderNotificationMapper(DateGenerator dateGenerator, String dateFormat, String senderEmail) {
        super(dateGenerator, dateFormat, senderEmail);
    }

    @Override
    OrderModel generateEmailData(OrdersApi order) {
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
