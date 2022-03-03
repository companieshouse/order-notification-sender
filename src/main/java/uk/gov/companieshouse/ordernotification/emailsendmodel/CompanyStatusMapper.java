package uk.gov.companieshouse.ordernotification.emailsendmodel;

import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

import java.util.Map;

public class CompanyStatusMapper {

    private final Map<String, StatusMappable> statusMappingCommands;
    private final StatusMappable defaultMappingCommand;

    public CompanyStatusMapper(Map<String, StatusMappable> statusMappingCommands, StatusMappable defaultMappingCommand) {
        this.statusMappingCommands = statusMappingCommands;
        this.defaultMappingCommand = defaultMappingCommand;
    }

    public void map(CertificateItemOptionsApi source, CertificateOrderNotificationModel target) {
        this.statusMappingCommands.getOrDefault(source.getCompanyStatus(), defaultMappingCommand).map(source, target);
    }
}