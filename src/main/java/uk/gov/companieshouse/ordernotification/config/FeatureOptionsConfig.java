package uk.gov.companieshouse.ordernotification.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeatureOptionsConfig {
    @Value("${llp.certificate.orders.enabled:false}")
    private boolean llpCertificateOrdersEnabled;
    @Value("${lp.certificate.orders.enabled:false}")
    private boolean lpCertificateOrdersEnabled;

    @Bean
    public FeatureOptions featureOptions() {
        return new FeatureOptions(llpCertificateOrdersEnabled, lpCertificateOrdersEnabled);
    }
}
