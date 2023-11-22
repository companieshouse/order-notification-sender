package uk.gov.companieshouse.ordernotification.config;

import email.email_send;
import java.util.UUID;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.ordernotification.consumer.MessageDeserialiser;
import uk.gov.companieshouse.orders.OrderReceived;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Configuration
public class TestConfig {

    @Bean
    EmbeddedKafkaBroker embeddedKafkaBroker() {
        return new EmbeddedKafkaBroker(1);
    }

    @Bean
    KafkaConsumer<String, email_send> emailSendConsumer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers, EmbeddedKafkaBroker embeddedKafkaBroker, KafkaTopics kafkaTopics) {
        Map<String, Object> props = KafkaTestUtils.consumerProps(bootstrapServers, UUID.randomUUID().toString(), Boolean.toString(true));
        KafkaConsumer<String, email_send> kafkaConsumer = new KafkaConsumer<>(props,
                new StringDeserializer(),
                new MessageDeserialiser<>(email_send.class));

        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(kafkaConsumer, kafkaTopics.getEmailSend());
        return kafkaConsumer;
    }

    @Bean
    KafkaConsumer<String, OrderReceived> orderReceivedRetryConsumer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers, EmbeddedKafkaBroker embeddedKafkaBroker, KafkaTopics kafkaTopics) {
        Map<String, Object> props = KafkaTestUtils.consumerProps(bootstrapServers, UUID.randomUUID().toString(), Boolean.toString(true));
        KafkaConsumer<String, OrderReceived> kafkaConsumer = new KafkaConsumer<>(props,
                new StringDeserializer(),
                new MessageDeserialiser<>(OrderReceived.class));
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(kafkaConsumer, kafkaTopics.getOrderReceivedRetry());
        return kafkaConsumer;
    }

    @Bean
    KafkaConsumer<String, OrderReceived> orderReceivedErrorConsumer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers, EmbeddedKafkaBroker embeddedKafkaBroker, KafkaTopics kafkaTopics) {
        Map<String, Object> props = KafkaTestUtils.consumerProps(bootstrapServers, UUID.randomUUID().toString(), Boolean.toString(true));
        KafkaConsumer<String, OrderReceived> kafkaConsumer = new KafkaConsumer<>(props,
                new StringDeserializer(),
                new MessageDeserialiser<>(OrderReceived.class));
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(kafkaConsumer, kafkaTopics.getOrderReceivedError());
        return kafkaConsumer;
    }

    @Bean
    KafkaProducer<String, OrderReceived> myProducer(@Value("${spring.kafka.bootstrap-servers}") String bootstrapServers) {
        return createProducer(bootstrapServers, OrderReceived.class);
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
