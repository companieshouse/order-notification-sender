package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@Component
class MissingImageOrderModelFactory {

    private final FilingHistoryDescriptionProviderService providerService;
    private final EmailConfiguration emailConfiguration;
    private final OrdersApiDetailsCommonFieldsMapper commonFieldsMapper;

    public MissingImageOrderModelFactory(FilingHistoryDescriptionProviderService providerService,
                                         EmailConfiguration emailConfiguration,
                                         OrdersApiDetailsCommonFieldsMapper commonFieldsMapper) {
        this.providerService = providerService;
        this.emailConfiguration = emailConfiguration;
        this.commonFieldsMapper = commonFieldsMapper;
    }

    MissingImageOrderNotificationModel newInstance(OrdersApiDetails order) {

        MissingImageOrderNotificationModel model = new MissingImageOrderNotificationModel();
        commonFieldsMapper.mapCommonFields(model, order);
        MissingImageDeliveryItemOptionsApi itemOptions = (MissingImageDeliveryItemOptionsApi) order.getBaseItemApi().getItemOptions();
        FilingHistoryDetailsModel filingHistoryDetailsModel = new FilingHistoryDetailsModel();
        filingHistoryDetailsModel.setFilingHistoryDate(
                LocalDate.parse(itemOptions.getFilingHistoryDate()).format(DateTimeFormatter.ofPattern(emailConfiguration.getMissingImage().getFilingHistoryDateFormat()))
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
}
