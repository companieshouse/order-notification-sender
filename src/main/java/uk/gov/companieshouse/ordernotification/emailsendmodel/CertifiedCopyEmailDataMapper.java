package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.FilingHistoryDocumentApi;
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
        FilingHistoryDocumentApi filingHistoryDocumentApi =
                itemOptions.getFilingHistoryDocuments().get(0);
        return CertifiedCopy.builder()
                .withId(certifiedDocumentItem.getId())
                .withCompanyNumber(certifiedDocumentItem.getCompanyNumber())
                .withDeliveryMethod(deliveryMethodMapper.mapDeliveryMethod(
                        itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale()))
                .withDateFiled(LocalDate.parse(filingHistoryDocumentApi.getFilingHistoryDate())
                        .format(DateTimeFormatter.ofPattern(
                                emailConfiguration.getFilingHistoryDateFormat())))
                .withType(filingHistoryDocumentApi.getFilingHistoryType())
                .withDescription(providerService.mapFilingHistoryDescription(
                        filingHistoryDocumentApi.getFilingHistoryDescription(),
                        filingHistoryDocumentApi.getFilingHistoryDescriptionValues()))
                .withFee("£" + filingHistoryDocumentApi.getFilingHistoryCost())
            .build();
    }
}
