package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DocumentOrderNotificationMapper extends OrdersApiMapper {

    private final String messageId;
    private final String applicationId;
    private final String messageType;
    private final FilingHistoryDescriptionProviderService providerService;

    public DocumentOrderNotificationMapper(DateGenerator dateGenerator, @Value("${email.date.format}") String dateFormat,
                                           @Value("${email.sender.address}") String senderEmail, @Value("${email.paymentDateFormat}") String paymentDateFormat,
                                           @Value("${email.document.messageId}") String messageId, @Value("${email.document.applicationId}") String applicationId,
                                           @Value("${email.document.messageType}") String messageType, FilingHistoryDescriptionProviderService providerService) {
        super(dateGenerator, dateFormat, paymentDateFormat, senderEmail);
        this.messageId = messageId;
        this.applicationId = applicationId;
        this.messageType = messageType;
        this.providerService = providerService;
    }

    @Override
    OrderModel generateEmailData(BaseItemApi order) {
        DocumentOrderNotificationModel model = new DocumentOrderNotificationModel();

        CertifiedCopyItemOptionsApi itemOptions = (CertifiedCopyItemOptionsApi) order.getItemOptions();
        Optional.ofNullable(itemOptions.getDeliveryMethod()).ifPresent(method -> model.setDeliveryMethod(method.getJsonName()));

        List<FilingHistoryDetailsModel> detailsModels = itemOptions.getFilingHistoryDocuments()
                .stream()
                .map(filingHistoryDocumentApi -> {
                    FilingHistoryDetailsModel details = new FilingHistoryDetailsModel();
                    details.setFilingHistoryDate(filingHistoryDocumentApi.getFilingHistoryDate());
                    details.setFilingHistoryCost(filingHistoryDocumentApi.getFilingHistoryCost());
                    details.setFilingHistoryDescription(
                            this.providerService.mapFilingHistoryDescription(
                                    filingHistoryDocumentApi.getFilingHistoryDescription(),
                                    filingHistoryDocumentApi.getFilingHistoryDescriptionValues()
                            )
                    );
                    details.setFilingHistoryType(filingHistoryDocumentApi.getFilingHistoryType());
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
