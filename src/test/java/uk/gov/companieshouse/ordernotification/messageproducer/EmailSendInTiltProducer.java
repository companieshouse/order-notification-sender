package uk.gov.companieshouse.ordernotification.messageproducer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.kafka.exceptions.ProducerConfigException;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.producer.Acks;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.ordernotification.consumer.PartitionOffset;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtilsConfiguration;

/**
 * "Test" class re-purposed to produce {@link EmailSend} messages to the
 * <code>email-send</code> topic in Tilt. This is NOT to be run as part of an
 * automated test suite. It is for manual testing only.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:email-send-in-tilt.properties")
@Import({MessageProducer.class, MessageFactory.class, KafkaProducer.class,
    LoggingUtilsConfiguration.class, EmailSendInTiltProducer.Config.class})
@SuppressWarnings("squid:S3577") // This is NOT to be run as part of an automated test suite.
class EmailSendInTiltProducer {

    private static final String TOPIC = "email-send";

    private static final EmailSend EMAIL_SEND;

    static {
        EmailSend emailSend = new EmailSend();
        emailSend.setAppId("App Id");
        emailSend.setData("Message content");
        emailSend.setEmailAddress("someone@example.com");
        emailSend.setMessageId("Message Id");
        emailSend.setMessageType("Message type");
        emailSend.setCreatedAt("2020-08-25T09:27:09.519+01:00");
        EMAIL_SEND = emailSend;
    }

    @Configuration
    static class Config {

        @Bean
        public Logger logger() {
            return LoggerFactory.getLogger(
                "EmailSendInTiltProducer");
        }

        @Bean
        public CHKafkaProducer chKafkaProducer(ProducerConfig producerConfig) {
            return new CHKafkaProducer(producerConfig);
        }

        @Bean
        public ProducerConfig producerConfig(
            @Value("${spring.kafka.bootstrap-servers}") String brokerAddresses) {
            final ProducerConfig config = new ProducerConfig();
            if (brokerAddresses != null && !brokerAddresses.isEmpty()) {
                config.setBrokerAddresses(brokerAddresses.split(","));
            } else {
                throw new ProducerConfigException(
                    "Broker addresses for kafka broker missing, check if environment variable KAFKA_BROKER_ADDR is configured. "
                        +
                        "[Hint: The property 'kafka.broker.addresses' uses the value of this environment variable in live environments "
                        +
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
        SerializerFactory serializerFactory() {
            return new SerializerFactory();
        }

    }

    @Autowired
    private MessageProducer messageProducer;

    @SuppressWarnings("squid:S2699") // at least one assertion
    @Test
    void produceMessageToTilt()
        throws InterruptedException, ExecutionException, TimeoutException, SerializationException {
        messageProducer.sendMessage(EMAIL_SEND, "order URI", TOPIC);
    }

}
