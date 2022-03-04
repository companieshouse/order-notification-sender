package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KindMapperFactoryConfiguration {
    private static final String CERTIFICATE = "item#certificate";
    private static final String CERTIFIED_COPY = "item#certified-copy";
    private static final String MISSING_IMAGE_DELIVERY = "item#missing-image-delivery";

    @Bean
    KindMapperFactory kindMapperFactory(CertificateOrderNotificationMapper certificateMapper,
                                        DocumentOrderNotificationMapper documentMapper,
                                        MissingImageOrderNotificationMapper missingImageMapper) {
        return KindMapperFactoryBuilder.newBuilder()
                .putKindMapper(CERTIFICATE, certificateMapper)
                .putKindMapper(CERTIFIED_COPY, documentMapper)
                .putKindMapper(MISSING_IMAGE_DELIVERY, missingImageMapper)
                .build();
    }
}
