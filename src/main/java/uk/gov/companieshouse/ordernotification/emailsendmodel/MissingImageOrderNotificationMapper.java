package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryItemOptionsApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class MissingImageOrderNotificationMapper extends OrdersApiMapper {

    private final String messageId;
    private final String applicationId;
    private final String messageType;
    private final String confirmationMessage;
    private final String filingHistoryDateFormat;
    private final FilingHistoryDescriptionProviderService providerService;

    @Autowired
    public MissingImageOrderNotificationMapper(DateGenerator dateGenerator, @Value("${email.dateFormat}") String dateFormat,
                                               @Value("${email.senderAddress}") String senderEmail,
                                               @Value("${email.paymentDateFormat}") String paymentDateFormat,
                                               @Value("${email.missing-image.messageId}") String messageId,
                                               @Value("${email.applicationId}") String applicationId,
                                               @Value("${email.missing-image.messageType}") String messageType,
                                               @Value("${email.confirmationMessage}") String confirmationMessage,
                                               @Value("${email.missing-image.filingHistoryDateFormat}") String filingHistoryDateFormat,
                                               FilingHistoryDescriptionProviderService providerService,
                                               ObjectMapper mapper) {
        super(dateGenerator, dateFormat, paymentDateFormat, senderEmail, mapper);
        this.messageId = messageId;
        this.applicationId = applicationId;
        this.messageType = messageType;
        this.confirmationMessage = confirmationMessage;
        this.filingHistoryDateFormat = filingHistoryDateFormat;
        this.providerService = providerService;
    }

    @Override
    OrderModel generateEmailData(BaseItemApi order) {

        MissingImageOrderNotificationModel model = new MissingImageOrderNotificationModel();
        MissingImageDeliveryItemOptionsApi itemOptions = (MissingImageDeliveryItemOptionsApi) order.getItemOptions();
        FilingHistoryDetailsModel filingHistoryDetailsModel = new FilingHistoryDetailsModel();
        filingHistoryDetailsModel.setFilingHistoryDate(
                LocalDate.parse(itemOptions.getFilingHistoryDate()).format(DateTimeFormatter.ofPattern(filingHistoryDateFormat))
        );
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

    @Override
    String getMessageSubject() {
        return this.confirmationMessage;
    }
}
