package uk.gov.companieshouse.ordernotification.emailsender;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.kafka.exceptions.ProducerConfigException;
import uk.gov.companieshouse.kafka.producer.Acks;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;
import uk.gov.companieshouse.ordernotification.config.KafkaBrokerConfiguration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests the {@link KafkaProducer} class.
 */
@ExtendWith(MockitoExtension.class)
class KafkaProducerConfigurationTest {

    private static final String EXPECTED_CONFIG_ERROR_MESSAGE =
        "Broker addresses for kafka broker missing, check if environment variable KAFKA_BROKER_ADDR is configured. " +
                "[Hint: The property 'kafka.broker.addresses' uses the value of this environment variable in live " +
                "environments and that of 'spring.embedded.kafka.brokers' property in test.]";


    @Test
    void afterPropertiesSetThrowsExceptionIfNoBrokersConfigured() {
        // Given
        KafkaBrokerConfiguration configuration = new KafkaBrokerConfiguration(null);

        // When
        ProducerConfigException exception = Assertions.assertThrows(ProducerConfigException.class, () ->
                configuration.chKafkaProducer(configuration.producerConfig()));

        // Then
        final String actualMessage = exception.getMessage();
        assertThat(actualMessage, is(EXPECTED_CONFIG_ERROR_MESSAGE));
    }

    @Test
    void afterPropertiesSetSetsProducerConfigProperties() {

        // Given
        String brokerAddress = "broker-address";
        KafkaBrokerConfiguration configuration = new KafkaBrokerConfiguration(brokerAddress);

        // When
        ProducerConfig producerConfig = configuration.producerConfig();

        // Then
        assertTrue(producerConfig.isRoundRobinPartitioner());
        assertEquals(Acks.WAIT_FOR_ALL, producerConfig.getAcks());
        assertEquals(10, producerConfig.getRetries());
    }

}
