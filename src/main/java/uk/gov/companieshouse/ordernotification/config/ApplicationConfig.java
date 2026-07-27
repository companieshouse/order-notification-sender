package uk.gov.companieshouse.ordernotification.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;
import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;

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

    @Bean("snakeCaseMapper")
    ObjectMapper objectMapper() {
        return JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withContentInclusion(JsonInclude.Include.NON_NULL)
                        .withValueInclusion(JsonInclude.Include.NON_NULL))
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                .build();
    }

    @Bean
    Map<CertificateTypeApi, String> certificateTypeMappings() {
        Map<CertificateTypeApi, String> mappings = new EnumMap<>(CertificateTypeApi.class);
        mappings.put(CertificateTypeApi.INCORPORATION, "Incorporation");
        mappings.put(CertificateTypeApi.DISSOLUTION, "Dissolution with all company name changes");
        mappings.put(CertificateTypeApi.INCORPORATION_WITH_ALL_NAME_CHANGES, "Incorporation with all company name changes");
        mappings.put(CertificateTypeApi.INCORPORATION_WITH_LAST_NAME_CHANGES, "Incorporation with last name changes");
        return mappings;
    }

    @Bean
    Map<IncludeAddressRecordsTypeApi, String> incorporationAddressMappings() {
        Map<IncludeAddressRecordsTypeApi, String> mappings = new EnumMap<>(IncludeAddressRecordsTypeApi.class);
        mappings.put(IncludeAddressRecordsTypeApi.CURRENT, "Current address");
        mappings.put(IncludeAddressRecordsTypeApi.CURRENT_AND_PREVIOUS, "Current address and the one previous");
        mappings.put(IncludeAddressRecordsTypeApi.CURRENT_PREVIOUS_AND_PRIOR, "Current address and the two previous");
        mappings.put(IncludeAddressRecordsTypeApi.ALL, "All current and previous addresses");
        return mappings;
    }

    @Bean
    Map<DeliveryTimescaleApi, String> deliveryMethodMappings() {
        Map<DeliveryTimescaleApi, String> mappings = new EnumMap<>(DeliveryTimescaleApi.class);
        mappings.put(DeliveryTimescaleApi.STANDARD, "Standard");
        mappings.put(DeliveryTimescaleApi.SAME_DAY, "Express");
        return mappings;
    }

    @Bean
    Supplier<InternalApiClient> internalApiClientSupplier(@Value("${chs.kafka.api.key}") final String chsKafkaApiKey,
            @Value("${chs.kafka.api.url}") final String chsKafkaApiUrl) {
        return () -> {
            InternalApiClient internalApiClient = new InternalApiClient(new ApiKeyHttpClient(chsKafkaApiKey));
            internalApiClient.setBasePath(chsKafkaApiUrl);

            return internalApiClient;
        };
    }
}
