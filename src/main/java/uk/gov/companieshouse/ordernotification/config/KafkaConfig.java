package uk.gov.companieshouse.ordernotification.config;

import static uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend.InvalidMessageRouter.INVALID_MESSAGE_TOPIC;
import static uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend.InvalidMessageRouter.MESSAGE_FLAGS;

import consumer.deserialization.AvroDeserializer;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.KafkaListenerErrorHandler;
import org.springframework.kafka.retrytopic.RetryTopicHeaders;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.messaging.handler.annotation.Header;
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

//    @Bean
//    public ConsumerFactory<String, ItemGroupProcessedSend> itemGroupProcessedSendConsumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
//            new MessageDeserialiser<>(ItemGroupProcessedSend.class));
//    }

    @Bean
    public ConsumerFactory<String, ItemGroupProcessedSend> itemGroupProcessedSendConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfigs2(), new StringDeserializer(),
            new ErrorHandlingDeserializer<>(new AvroDeserializer<>(ItemGroupProcessedSend.class)));
    }
//
//    @Bean
//    KafkaListenerErrorHandler eh(DeadLetterPublishingRecoverer recoverer) {
//        return (msg, ex) -> {
//            if (msg.getHeaders().get(KafkaHeaders.DELIVERY_ATTEMPT, Integer.class) > 9) {
//                recoverer.accept(msg.getHeaders().get(KafkaHeaders.RAW_DATA, ConsumerRecord.class), ex);
//                return "FAILED";
//            }
//            throw ex;
//        };
//    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderReceived> kafkaOrderReceivedListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, OrderReceived> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderReceivedConsumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0, 0)));
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ItemGroupProcessedSend> kafkaItemGroupProcessedSendListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ItemGroupProcessedSend> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(itemGroupProcessedSendConsumerFactory());
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0, 0)));
        // factory.getContainerProperties().setDeliveryAttemptHeader(true);
        return factory;
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddresses);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserialiser.class);
        return props;
    }

    private Map<String, Object> consumerConfigs2() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerAddresses);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, MessageDeserialiser.class);

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, AvroDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
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

        final Map<String, Object> producerFactoryConfig = new HashMap<>();
        producerFactoryConfig.put(
            org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
            bootstrapServers);
        producerFactoryConfig.put(org.apache.kafka.clients.producer.ProducerConfig.ACKS_CONFIG,
            "all");
        producerFactoryConfig.put(
            org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
        producerFactoryConfig.put(
            org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
            StringSerializer.class);
        producerFactoryConfig.put(
            org.apache.kafka.clients.producer.ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,
            InvalidMessageRouter.class.getName());
        producerFactoryConfig.put(MESSAGE_FLAGS, messageFlags);
        producerFactoryConfig.put(INVALID_MESSAGE_TOPIC, invalidMessageTopic);

        return new DefaultKafkaProducerFactory<>(
            producerFactoryConfig,
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

    @KafkaListener(id = "item-group-processed-send", topics = "item-group-processed-send")
    void listen(String in, @Header(name = RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS, required = false) Integer attempts) {
        logger.info("listen(" + in + ", " + attempts + ")");
    }

    @KafkaListener(id = "item-group-processed-send-retry", topics = "item-group-processed-send-retry")
    void listenRetry(String in, @Header(name = RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS, required = false) Integer attempts) {
        logger.info("listenRetry(" + in + ", " + attempts + ")");
    }

    @KafkaListener(id = "item-group-processed-send-error", topics = "item-group-processed-send-error")
    void listenError(String in, @Header(name = RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS, required = false) Integer attempts) {
        logger.info("listenError(" + in + ", " + attempts + ")");
    }

    @KafkaListener(id = "item-group-processed-send-invalid", topics = "item-group-processed-send-invalid")
    void listenInvalid(String in, @Header(name = RetryTopicHeaders.DEFAULT_HEADER_ATTEMPTS, required = false) Integer attempts) {
        logger.info("listenInvalid(" + in + ", " + attempts + ")");
    }
}
