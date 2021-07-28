package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryItemOptionsApi;

public class MissingImageOrderNotificationMapper extends OrdersApiMapper {

    private final String messageId;
    private final String applicationId;
    private final String messageType;

    public MissingImageOrderNotificationMapper(DateGenerator dateGenerator, @Value("${email.date.format}") String dateFormat,
                                               @Value("${email.sender.address}") String senderEmail, @Value("${email.paymentDateFormat}") String paymentDateFormat,
                                               @Value("${email.document.messageId}") String messageId, @Value("${email.document.applicationId}") String applicationId,
                                               @Value("${email.document.messageType}") String messageType) {
        super(dateGenerator, dateFormat, paymentDateFormat, senderEmail);
        this.messageId = messageId;
        this.applicationId = applicationId;
        this.messageType = messageType;
    }

    @Override
    OrderModel generateEmailData(BaseItemApi order) {

        MissingImageOrderNotificationModel model = new MissingImageOrderNotificationModel();
        MissingImageDeliveryItemOptionsApi itemOptions = (MissingImageDeliveryItemOptionsApi) order.getItemOptions();
        FilingHistoryDetailsModel filingHistoryDetailsModel = new FilingHistoryDetailsModel();
        filingHistoryDetailsModel.setFilingHistoryDate(itemOptions.getFilingHistoryDate());
        filingHistoryDetailsModel.setFilingHistoryType(itemOptions.getFilingHistoryType());
        filingHistoryDetailsModel.setFilingHistoryDescription(itemOptions.getFilingHistoryDescription());

        model.setFilingHistoryDetails(filingHistoryDetailsModel);

        return model;
    }

    @Override
    String getMessageId() {
        return this.messageId;
    }

    @Override
    String getApplicationId() {
        return this.applicationId;
    }

    @Override
    String getMessageType() {
        return this.messageType;
    }
}
