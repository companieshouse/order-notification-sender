package uk.gov.companieshouse.ordernotification.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.kafka.exceptions.ProducerConfigException;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.producer.Acks;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.util.DataMap;
import uk.gov.companieshouse.ordernotification.consumer.MessageDeserialiser;
import uk.gov.companieshouse.ordernotification.consumer.PartitionOffset;
import uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend.InvalidMessageRouter;
import uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend.MessageFlags;
import uk.gov.companieshouse.orders.OrderReceived;

@Configuration
public class KafkaConfig {

    private final String brokerAddresses;
    private final Logger logger;

    public KafkaConfig(@Value("${spring.kafka.bootstrap-servers}") String brokerAddresses,
        Logger logger) {
        this.brokerAddresses = brokerAddresses;
        this.logger = logger;
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
    public ConsumerFactory<String, ItemGroupProcessedSend> itemGroupProcessedSendConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
                new MessageDeserialiser<>(ItemGroupProcessedSend.class));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderReceived> kafkaOrderReceivedListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderReceived> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderReceivedConsumerFactory());
        factory.setErrorHandler(new SeekToCurrentErrorHandler(new FixedBackOff(0, 0)));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ItemGroupProcessedSend> kafkaItemGroupProcessedSendListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ItemGroupProcessedSend> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(itemGroupProcessedSendConsumerFactory());
        factory.setErrorHandler(new SeekToCurrentErrorHandler(new FixedBackOff(0, 0)));
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

    @Bean
    public PartitionOffset errorRecoveryOffset() {
        return new PartitionOffset();
    }

    @Bean
    @ConfigurationProperties(prefix = "kafka.topics")
    KafkaTopics kafkaTopics() {
        return new KafkaTopics();
    }

    @Bean
    // In the unlikely event that a SerializationException should occur, the RuntimeException used to wrap
    // it is swallowed by spring/spring-kafka to which our exception types would be meaningless.
    @SuppressWarnings("squid:S112")
    public ProducerFactory<String, ItemGroupProcessedSend> producerFactory(
        @Value("${spring.kafka.bootstrap-servers}") String bootstrapServers,
        MessageFlags messageFlags,
        @Value("${kafka.topics.item-group-processed-send.invalid_message_topic}") String invalidMessageTopic) {

        return new DefaultKafkaProducerFactory<>(
            new HashMap<String, Object>() {
                {
                    put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                        bootstrapServers);
                    put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG, "all");
                    put(org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                        StringSerializer.class);
                    put(org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                        StringSerializer.class);
                    put(org.apache.kafka.clients.producer.ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
                        InvalidMessageRouter.class.getName());
                    put("message.flags", messageFlags);
                    put("invalid.message.topic", invalidMessageTopic);
                }
            },
            new StringSerializer(),
            (topic, data) -> {
                try {
                    return new SerializerFactory().getSpecificRecordSerializer(
                            ItemGroupProcessedSend.class)
                        .toBinary(data); //creates a leading space
                } catch (SerializationException e) {
                    final DataMap dataMap = new DataMap.Builder()
                        .topic(topic)
                        .kafkaMessage(data.toString())
                        .build();
                    logger.error("Caught SerializationException serializing kafka message: "
                            + e.getMessage(),
                        dataMap.getLogMap());
                    throw new RuntimeException(e);
                }
            }
        );
    }

    @Bean
    public KafkaTemplate<String, ItemGroupProcessedSend> kafkaTemplate(
        ProducerFactory<String, ItemGroupProcessedSend> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
