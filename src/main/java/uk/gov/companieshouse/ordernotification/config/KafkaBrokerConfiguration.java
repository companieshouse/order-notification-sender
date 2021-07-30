package uk.gov.companieshouse.ordernotification.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import uk.gov.companieshouse.kafka.exceptions.ProducerConfigException;
import uk.gov.companieshouse.kafka.producer.Acks;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;
import uk.gov.companieshouse.ordernotification.ordersconsumer.MessageDeserialiser;
import uk.gov.companieshouse.orders.OrderReceived;
import uk.gov.companieshouse.orders.OrderReceivedNotificationRetry;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaBrokerConfiguration {

    private String brokerAddresses;

    public KafkaBrokerConfiguration(@Value("${spring.kafka.bootstrap-servers}") String brokerAddresses) {
        this.brokerAddresses = brokerAddresses;
    }

    @Bean
    public CHKafkaProducer chKafkaProducer(ProducerConfig producerConfig) {
        return new CHKafkaProducer(producerConfig);
    }

    @Bean
    public ConsumerFactory<String, OrderReceived> orderReceivedConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
                new MessageDeserialiser<>(OrderReceived.class));
    }

    @Bean
    public ConsumerFactory<String, OrderReceivedNotificationRetry> orderReceivedRetryConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
                new MessageDeserialiser<>(OrderReceivedNotificationRetry.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderReceived> kafkaOrderReceivedListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderReceived> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderReceivedConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderReceivedNotificationRetry> kafkaOrderReceivedRetryListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderReceivedNotificationRetry> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderReceivedRetryConsumerFactory());
        return factory;
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddresses);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserialiser.class);
        return props;
    }

    @Bean
    public ProducerConfig producerConfig() {
        final ProducerConfig config = new ProducerConfig();
        if (brokerAddresses != null && !brokerAddresses.isEmpty()) {
            config.setBrokerAddresses(brokerAddresses.split(","));
        } else {
            throw new ProducerConfigException("Broker addresses for kafka broker missing, check if environment variable KAFKA_BROKER_ADDR is configured. " +
                    "[Hint: The property 'kafka.broker.addresses' uses the value of this environment variable in live environments " +
                    "and that of 'spring.embedded.kafka.brokers' property in test.]");
        }
        config.setRoundRobinPartitioner(true);
        config.setAcks(Acks.WAIT_FOR_ALL);
        config.setRetries(10);
        return config;
    }
}
