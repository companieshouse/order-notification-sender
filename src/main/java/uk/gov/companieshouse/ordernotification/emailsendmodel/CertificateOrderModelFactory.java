package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@Component
public class CertificateOrderModelFactory {
    private final CertificateOptionsMapperFactory certificateOptionsMapperFactory;
    private final EmailConfiguration emailConfiguration;
    private final OrdersApiDetailsCommonFieldsMapper commonFieldsMapper;

    public CertificateOrderModelFactory(CertificateOptionsMapperFactory certificateOptionsMapperFactory,
                                        EmailConfiguration emailConfiguration,
                                        OrdersApiDetailsCommonFieldsMapper commonFieldsMapper) {
        this.certificateOptionsMapperFactory = certificateOptionsMapperFactory;
        this.emailConfiguration = emailConfiguration;
        this.commonFieldsMapper = commonFieldsMapper;
    }

    public CertificateOrderNotificationModel newInstance(OrdersApiDetails ordersApiDetails) {
        BaseItemApi baseItemApi = ordersApiDetails.getBaseItemApi();
        CertificateItemOptionsApi certificateItemOptionsApi =
                (CertificateItemOptionsApi) baseItemApi.getItemOptions();
        CertificateOrderNotificationModel orderModel = certificateOptionsMapperFactory
                .getCertificateOptionsMapper(certificateItemOptionsApi.getCompanyType())
                .generateEmailData(baseItemApi);
        commonFieldsMapper.mapCommonFields(orderModel, ordersApiDetails);
        return orderModel;
    }
}
