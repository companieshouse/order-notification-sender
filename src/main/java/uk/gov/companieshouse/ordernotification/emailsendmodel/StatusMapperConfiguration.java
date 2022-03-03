package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StatusMapperConfiguration {

    @Autowired
    private AdministrationStatusMapper administrationStatusMapper;

    @Autowired
    private LiquidationStatusMapper liquidationStatusMapper;

    @Autowired
    private DefaultStatusMapper defaultStatusMapper;

    @Bean
    CompanyStatusMapper companyStatusMapper(@Qualifier("companyStatusMappings") Map<String, StatusMappable> companyStatusMappings) {
        return new CompanyStatusMapper(companyStatusMappings, defaultStatusMapper);
    }

    @Bean
    Map<String, StatusMappable> companyStatusMappings() {
        return new HashMap<String, StatusMappable>(){{
            put("administration", administrationStatusMapper);
            put("liquidation", liquidationStatusMapper);
        }};
    }
}
