package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentOrderNotificationMapper extends OrdersApiMapper {

    private final String messageId;
    private final String applicationId;
    private final String messageType;
    private final String confirmationMessage;
    private final String filingHistoryDateFormat;
    private final FilingHistoryDescriptionProviderService providerService;
    private final DeliveryMethodMapper deliveryMethodMapper;

    @Autowired
    public DocumentOrderNotificationMapper(DateGenerator dateGenerator, @Value("${email.dateFormat}") String dateFormat,
                                           @Value("${email.senderAddress}") String senderEmail, @Value("${email.paymentDateFormat}") String paymentDateFormat,
                                           @Value("${email.document.messageId}") String messageId, @Value("${email.applicationId}") String applicationId,
                                           @Value("${email.document.messageType}") String messageType, @Value("${email.confirmationMessage}") String confirmationMessage,
                                           @Value("${email.document.filingHistoryDateFormat}") String filingHistoryDateFormat,
                                           FilingHistoryDescriptionProviderService providerService, ObjectMapper mapper,
                                           DeliveryMethodMapper deliveryMethodMapper) {
        super(dateGenerator, dateFormat, paymentDateFormat, senderEmail, mapper);
        this.messageId = messageId;
        this.applicationId = applicationId;
        this.messageType = messageType;
        this.confirmationMessage = confirmationMessage;
        this.filingHistoryDateFormat = filingHistoryDateFormat;
        this.providerService = providerService;
        this.deliveryMethodMapper = deliveryMethodMapper;
    }

    @Override
    OrderModel generateEmailData(BaseItemApi order) {
        DocumentOrderNotificationModel model = new DocumentOrderNotificationModel();

        CertifiedCopyItemOptionsApi itemOptions = (CertifiedCopyItemOptionsApi) order.getItemOptions();
        model.setDeliveryMethod(deliveryMethodMapper.mapDeliveryMethod(itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale()));

        List<FilingHistoryDetailsModel> detailsModels = itemOptions.getFilingHistoryDocuments()
                .stream()
                .map(filingHistoryDocumentApi -> {
                    FilingHistoryDetailsModel details = new FilingHistoryDetailsModel();
                    details.setFilingHistoryDate(LocalDate.parse(filingHistoryDocumentApi.getFilingHistoryDate())
                            .format(DateTimeFormatter.ofPattern(filingHistoryDateFormat)));
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

    @Override
    String getMessageSubject() {
        return confirmationMessage;
    }
}
