package uk.gov.companieshouse.ordernotification.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryApi;
import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.ordernotification.emailsendmodel.CertificateOrderNotificationMapper;
import uk.gov.companieshouse.ordernotification.emailsendmodel.DocumentOrderNotificationMapper;
import uk.gov.companieshouse.ordernotification.emailsendmodel.MissingImageOrderNotificationMapper;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrderMapperFactory;
import uk.gov.companieshouse.ordernotification.emailsendmodel.OrdersApiMapper;

import java.util.HashMap;
import java.util.Map;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;

@Configuration
public class ApplicationConfig implements WebMvcConfigurer {
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
    public Map<Class<? extends BaseItemApi>, OrdersApiMapper> ordersApiMappers(CertificateOrderNotificationMapper certificateMapper,
                                                                               DocumentOrderNotificationMapper documentMapper,
                                                                               MissingImageOrderNotificationMapper missingImageMapper) {
        Map<Class<? extends BaseItemApi>, OrdersApiMapper> mappers = new HashMap<>();
        mappers.put(CertificateApi.class, certificateMapper);
        mappers.put(CertifiedCopyApi.class, documentMapper);
        mappers.put(MissingImageDeliveryApi.class, missingImageMapper);
        return mappers;
    }
}
