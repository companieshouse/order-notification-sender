package uk.gov.companieshouse.ordernotification.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;
import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.ordernotification.emailsendmodel.CertificateOrderNotificationMapper;
import uk.gov.companieshouse.ordernotification.emailsendmodel.DocumentOrderNotificationMapper;
import uk.gov.companieshouse.ordernotification.emailsendmodel.MissingImageOrderNotificationMapper;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrdersApiMapper;

import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {

    private static final String CERTIFICATE = "item#certificate";
    private static final String CERTIFIED_COPY = "item#certified-copy";
    private static final String MISSING_IMAGE_DELIVERY = "item#missing-image-delivery";

    @Bean
    DeserializerFactory deserializerFactory() {
        return new DeserializerFactory();
    }

    @Bean
    SerializerFactory serializerFactory() {
        return new SerializerFactory();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setPropertyNamingStrategy(SNAKE_CASE)
                .findAndRegisterModules();
    }

    @Bean
    public Map<String, OrdersApiMapper> ordersApiMappers(CertificateOrderNotificationMapper certificateMapper,
                                                                               DocumentOrderNotificationMapper documentMapper,
                                                                               MissingImageOrderNotificationMapper missingImageMapper) {
        Map<String, OrdersApiMapper> mappers = new HashMap<>();
        mappers.put(CERTIFICATE, certificateMapper);
        mappers.put(CERTIFIED_COPY, documentMapper);
        mappers.put(MISSING_IMAGE_DELIVERY, missingImageMapper);
        return mappers;
    }

    @Bean
    public Map<CertificateTypeApi, String> certificateTypeMappings() {
        Map<CertificateTypeApi, String> mappings = new HashMap<>();
        mappings.put(CertificateTypeApi.INCORPORATION, "Incorporation");
        mappings.put(CertificateTypeApi.DISSOLUTION, "Dissolution with all company name changes");
        mappings.put(CertificateTypeApi.INCORPORATION_WITH_ALL_NAME_CHANGES, "Incorporation with all company name changes");
        mappings.put(CertificateTypeApi.INCORPORATION_WITH_LAST_NAME_CHANGES, "Incorporation with last name changes");
        return mappings;
    }
}
