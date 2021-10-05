package uk.gov.companieshouse.ordernotification.config;

import email.email_send;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.ordernotification.emailsendmodel.CertificateOptionsMapperFactory;
import uk.gov.companieshouse.ordernotification.ordersconsumer.MessageDeserialiser;
import uk.gov.companieshouse.orders.OrderReceived;
import uk.gov.companieshouse.orders.OrderReceivedNotificationRetry;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Configuration
@Import({CertificateOptionsMapperFactory.class, FeatureOptionsConfig.class})
public class TestConfig {

    @Bean
    EmbeddedKafkaBroker embeddedKafkaBroker() {
        return new EmbeddedKafkaBroker(1);
    }

    @Bean
    KafkaProducer<String, OrderReceived> myProducer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return createProducer(bootstrapServers, OrderReceived.class);
    }

    @Bean
    KafkaProducer<String, OrderReceivedNotificationRetry> myRetryProducer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return createProducer(bootstrapServers, OrderReceivedNotificationRetry.class);
    }

    private <T extends SpecificRecord> KafkaProducer<String, T> createProducer(String bootstrapServers, Class<T> type) {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaProducer<>(config, new StringSerializer(), (topic, data) -> {
            try {
                return new SerializerFactory().getSpecificRecordSerializer(type).toBinary(data); //creates a leading space
            } catch (SerializationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Bean
    @Scope("prototype")
    KafkaConsumer<String, email_send> myConsumer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserialiser.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, ""+new Random().nextInt());
        return new KafkaConsumer<>(props, new StringDeserializer(), new MessageDeserialiser<>(email_send.class));
    }
}
