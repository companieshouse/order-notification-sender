package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;

import java.util.List;
import java.util.stream.Collectors;

public class DocumentOrderNotificationMapper extends OrdersApiMapper {

    private final String messageId;
    private final String applicationId;
    private final String messageType;

    public DocumentOrderNotificationMapper(DateGenerator dateGenerator, @Value("${email.date.format}") String dateFormat,
                                           @Value("${email.sender.address}") String senderEmail, @Value("${email.paymentDateFormat}") String paymentDateFormat,
                                           @Value("${email.certificate.messageId}") String messageId, @Value("${email.certificate.applicationId}") String applicationId,
                                           @Value("${email.certificate.messageType}") String messageType) {
        super(dateGenerator, dateFormat, paymentDateFormat, senderEmail);
        this.messageId = messageId;
        this.applicationId = applicationId;
        this.messageType = messageType;
    }

    @Override
    OrderModel generateEmailData(BaseItemApi order) {
        DocumentOrderNotificationModel model = new DocumentOrderNotificationModel();

        CertifiedCopyItemOptionsApi itemOptions = (CertifiedCopyItemOptionsApi) order.getItemOptions();
        model.setDeliveryMethod(itemOptions.getDeliveryMethod().getJsonName());

        List<DocumentOrderDocumentDetailsModel> detailsModels = itemOptions.getFilingHistoryDocuments()
                .stream()
                .map(filingHistoryDocumentApi -> {
                    DocumentOrderDocumentDetailsModel details = new DocumentOrderDocumentDetailsModel();
                    details.setFilingHistoryDate(filingHistoryDocumentApi.getFilingHistoryDate());
                    details.setFilingHistoryCost(filingHistoryDocumentApi.getFilingHistoryCost());
                    details.setMadeUpDate(filingHistoryDocumentApi.getFilingHistoryDescriptionValues().get("made_up_date").toString());
                    details.setFilingHistoryType(filingHistoryDocumentApi.getFilingHistoryType());
                    details.setFilingHistoryDescription(filingHistoryDocumentApi.getFilingHistoryDescription());
                    return details;
                }).collect(Collectors.toList());

        model.setFilingHistoryDocuments(detailsModels);

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
