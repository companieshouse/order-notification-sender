package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@Component
public class CertificateOrderNotificationMapper extends OrdersApiMapper {

    private final CertificateOptionsMapperFactory certificateOptionsMapperFactory;

    @Autowired
    public CertificateOrderNotificationMapper(DateGenerator dateGenerator, EmailConfiguration config,
                                              ObjectMapper mapper,
                                              CertificateOptionsMapperFactory certificateOptionsMapperFactory) {
        super(dateGenerator, config, mapper);
        this.certificateOptionsMapperFactory = certificateOptionsMapperFactory;
    }

    @Override
    protected CertificateOrderNotificationModel generateEmailData(BaseItemApi item) {
        return certificateOptionsMapperFactory.getCertificateOptionsMapper(
                ((CertificateItemOptionsApi) item.getItemOptions()).getCompanyType()).generateEmailData(item);
    }

    @Override
    protected String getMessageId() {
        return getConfig().getCertificate().getMessageId();
    }

    @Override
    protected String getMessageType() {
        return getConfig().getCertificate().getMessageType();
    }
}
