package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

public class CertifiedCopyEmailDataMapper {
    private final DeliveryMethodMapper deliveryMethodMapper;
    private final FilingHistoryDescriptionProviderService providerService;
    private EmailConfiguration emailConfiguration;

    public CertifiedCopyEmailDataMapper(DeliveryMethodMapper deliveryMethodMapper,
            FilingHistoryDescriptionProviderService providerService,
            EmailConfiguration emailConfiguration) {
        this.deliveryMethodMapper = deliveryMethodMapper;
        this.providerService = providerService;
        this.emailConfiguration = emailConfiguration;
    }

    CertifiedCopy map(BaseItemApi certifiedDocumentItem) {
        CertifiedCopyItemOptionsApi itemOptions =
                (CertifiedCopyItemOptionsApi) certifiedDocumentItem.getItemOptions();

        return CertifiedCopy.builder()
            .withId(certifiedDocumentItem.getId())
            .withCompanyNumber(certifiedDocumentItem.getCompanyNumber())
            .withDeliveryMethod(deliveryMethodMapper.mapDeliveryMethod(
                    itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale()))
            .withFilingHistoryDetailsModelList(mapFilingHistoryDocuments(itemOptions))
            .build();
    }

    private List<FilingHistoryDetailsModel> mapFilingHistoryDocuments(
            CertifiedCopyItemOptionsApi itemOptions) {
        return itemOptions.getFilingHistoryDocuments().stream()
            .map(filingHistoryDocumentApi -> {
                FilingHistoryDetailsModel details = new FilingHistoryDetailsModel();
                details.setFilingHistoryDate(
                        LocalDate.parse(filingHistoryDocumentApi.getFilingHistoryDate())
                        .format(DateTimeFormatter.ofPattern(emailConfiguration.getDocument()
                                .getFilingHistoryDateFormat())));
                details.setFilingHistoryCost(
                        "£" + filingHistoryDocumentApi.getFilingHistoryCost());
                details.setFilingHistoryDescription(
                        this.providerService.mapFilingHistoryDescription(
                                filingHistoryDocumentApi.getFilingHistoryDescription(),
                                filingHistoryDocumentApi.getFilingHistoryDescriptionValues()
                        )
                );
                details.setFilingHistoryType(filingHistoryDocumentApi.getFilingHistoryType());
                return details;
            }).collect(Collectors.toList());
    }
}
