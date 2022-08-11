package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class MissingImageDeliveryEmailDataMapper {
    private final FilingHistoryDescriptionProviderService providerService;
    private final EmailConfiguration emailConfiguration;

    public MissingImageDeliveryEmailDataMapper(FilingHistoryDescriptionProviderService providerService,
                                               EmailConfiguration emailConfiguration) {
        this.providerService = providerService;
        this.emailConfiguration = emailConfiguration;
    }

    MissingImageDelivery map(BaseItemApi missingImageDeliveryItem) {
        MissingImageDeliveryItemOptionsApi itemOptions = (MissingImageDeliveryItemOptionsApi)
                missingImageDeliveryItem.getItemOptions();

        return MissingImageDelivery.builder()
                .withId(missingImageDeliveryItem.getId())
                .withDateFiled(LocalDate.parse(itemOptions.getFilingHistoryDate())
                        .format(DateTimeFormatter.ofPattern(
                                emailConfiguration.getFilingHistoryDateFormat())))
                .withType(itemOptions.getFilingHistoryType())
                .withDescription(providerService.mapFilingHistoryDescription(
                        itemOptions.getFilingHistoryDescription(),
                        itemOptions.getFilingHistoryDescriptionValues()))
                .withCompanyNumber(missingImageDeliveryItem.getCompanyNumber())
                .withFee("£" + missingImageDeliveryItem.getTotalItemCost())
                .build();
    }
}
