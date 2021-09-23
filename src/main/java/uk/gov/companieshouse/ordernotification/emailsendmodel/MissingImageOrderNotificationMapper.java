package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class MissingImageOrderNotificationMapper extends OrdersApiMapper {

    private final FilingHistoryDescriptionProviderService providerService;

    @Autowired
    public MissingImageOrderNotificationMapper(DateGenerator dateGenerator, EmailConfiguration config,
                                               FilingHistoryDescriptionProviderService providerService,
                                               ObjectMapper mapper) {
        super(dateGenerator, config, mapper);
        this.providerService = providerService;
    }

    @Override
    protected OrderModel generateEmailData(BaseItemApi order) {

        MissingImageOrderNotificationModel model = new MissingImageOrderNotificationModel();
        MissingImageDeliveryItemOptionsApi itemOptions = (MissingImageDeliveryItemOptionsApi) order.getItemOptions();
        FilingHistoryDetailsModel filingHistoryDetailsModel = new FilingHistoryDetailsModel();
        filingHistoryDetailsModel.setFilingHistoryDate(
                LocalDate.parse(itemOptions.getFilingHistoryDate()).format(DateTimeFormatter.ofPattern(getConfig().getMissingImage().getFilingHistoryDateFormat()))
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
    protected String getMessageId() {
        return getConfig().getMissingImage().getMessageId();
    }

    @Override
    protected String getMessageType() {
        return getConfig().getMissingImage().getMessageType();
    }
}
