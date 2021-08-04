package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocumentOrderNotificationMapper extends OrdersApiMapper {

    private final EmailConfiguration config;
    private final FilingHistoryDescriptionProviderService providerService;
    private final DeliveryMethodMapper deliveryMethodMapper;

    @Autowired
    public DocumentOrderNotificationMapper(DateGenerator dateGenerator, EmailConfiguration config,
                                           FilingHistoryDescriptionProviderService providerService, ObjectMapper mapper,
                                           DeliveryMethodMapper deliveryMethodMapper) {
        super(dateGenerator, config, mapper);
        this.config = config;
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
                            .format(DateTimeFormatter.ofPattern(config.getDocument().getFilingHistoryDateFormat())));
                    details.setFilingHistoryCost("£"+filingHistoryDocumentApi.getFilingHistoryCost());
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
        return config.getDocument().getMessageId();
    }

    @Override
    String getMessageType() {
        return config.getDocument().getMessageType();
    }
}
