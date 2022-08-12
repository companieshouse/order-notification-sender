package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.Map;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;
import uk.gov.companieshouse.api.model.order.item.FilingHistoryDocumentApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@Component
public class CertifiedCopyEmailDataMapper {
    private final Map<DeliveryTimescaleApi, String> deliveryMappings;
    private final FilingHistoryDescriptionProviderService providerService;
    private final EmailConfiguration emailConfiguration;

    public CertifiedCopyEmailDataMapper(@Qualifier("deliveryMethodMappings") Map<DeliveryTimescaleApi, String> deliveryMappings,
            FilingHistoryDescriptionProviderService providerService,
            EmailConfiguration emailConfiguration) {
        this.deliveryMappings = deliveryMappings;
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
                .withDeliveryMethod(deliveryMappings.get(itemOptions.getDeliveryTimescale()))
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
