package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryItemOptionsApi;

public class MissingImageOrderNotificationMapper extends OrdersApiMapper {

    private final String messageId;
    private final String applicationId;
    private final String messageType;
    private final FilingHistoryDescriptionProviderService providerService;

    public MissingImageOrderNotificationMapper(DateGenerator dateGenerator, @Value("${email.dateFormat}") String dateFormat,
                                               @Value("${email.senderAddress}") String senderEmail, @Value("${email.paymentDateFormat}") String paymentDateFormat,
                                               @Value("${email.missing-image.messageId}") String messageId, @Value("${email.applicationId}") String applicationId,
                                               @Value("${email.missing-image.messageType}") String messageType, FilingHistoryDescriptionProviderService providerService) {
        super(dateGenerator, dateFormat, paymentDateFormat, senderEmail);
        this.messageId = messageId;
        this.applicationId = applicationId;
        this.messageType = messageType;
        this.providerService = providerService;
    }

    @Override
    OrderModel generateEmailData(BaseItemApi order) {

        MissingImageOrderNotificationModel model = new MissingImageOrderNotificationModel();
        MissingImageDeliveryItemOptionsApi itemOptions = (MissingImageDeliveryItemOptionsApi) order.getItemOptions();
        FilingHistoryDetailsModel filingHistoryDetailsModel = new FilingHistoryDetailsModel();
        filingHistoryDetailsModel.setFilingHistoryDate(itemOptions.getFilingHistoryDate());
        filingHistoryDetailsModel.setFilingHistoryType(itemOptions.getFilingHistoryType());
        filingHistoryDetailsModel.setFilingHistoryDescription(
                providerService.mapFilingHistoryDescription(
                        itemOptions.getFilingHistoryDescription(), itemOptions.getFilingHistoryDescriptionValues()
                )
        );

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
