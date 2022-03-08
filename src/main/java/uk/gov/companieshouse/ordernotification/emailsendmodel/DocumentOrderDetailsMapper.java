package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@Component
class DocumentOrderDetailsMapper {
    private final EmailConfiguration emailConfiguration;
    private final DeliveryMethodMapper deliveryMethodMapper;
    private final FilingHistoryDescriptionProviderService providerService;
    private final OrdersApiDetailsCommonFieldsMapper commonFieldsMapper;

    DocumentOrderDetailsMapper(EmailConfiguration emailConfiguration,
                               DeliveryMethodMapper deliveryMethodMapper,
                               FilingHistoryDescriptionProviderService providerService,
                               OrdersApiDetailsCommonFieldsMapper commonFieldsMapper) {
        this.emailConfiguration = emailConfiguration;
        this.deliveryMethodMapper = deliveryMethodMapper;
        this.providerService = providerService;
        this.commonFieldsMapper = commonFieldsMapper;
    }

    DocumentOrderNotificationModel map(OrdersApiDetails order) {
        DocumentOrderNotificationModel model = new DocumentOrderNotificationModel();
        commonFieldsMapper.mapCommonFields(model, order);

        CertifiedCopyItemOptionsApi itemOptions = (CertifiedCopyItemOptionsApi) order.getItemOptions();
        model.setDeliveryMethod(deliveryMethodMapper.mapDeliveryMethod(itemOptions.getDeliveryMethod(),
                itemOptions.getDeliveryTimescale()));

        List<FilingHistoryDetailsModel> detailsModels = itemOptions.getFilingHistoryDocuments()
                .stream()
                .map(filingHistoryDocumentApi -> {
                    FilingHistoryDetailsModel details = new FilingHistoryDetailsModel();
                    details.setFilingHistoryDate(LocalDate.parse(filingHistoryDocumentApi.getFilingHistoryDate())
                            .format(DateTimeFormatter.ofPattern(emailConfiguration.getDocument()
                                    .getFilingHistoryDateFormat())));
                    details.setFilingHistoryCost("£" + filingHistoryDocumentApi.getFilingHistoryCost());
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
}
