package uk.gov.companieshouse.ordernotification.logging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingUtilsConfiguration {

    @Bean
    public LoggingUtils getLoggingUtils() {
        return new LoggingUtils();
    }
}
