package uk.gov.companieshouse.ordernotification.emailsender;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.ordernotification.config.KafkaBrokerConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests the {@link KafkaProducer} class.
 */
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@ContextConfiguration(classes = KafkaBrokerConfiguration.class)
@TestPropertySource(locations = "classpath:application-stubbed.properties")
class KafkaProducerConfigurationIntegrationTest {

    @Autowired
    private CHKafkaProducer chKafkaProducer;

    private static KafkaContainer kafkaContainer;

    @BeforeAll
    static void setupKafkaEnvironment() {
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
        kafkaContainer.start();
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
    }
    @AfterAll
    static void teardownKafkaEnvironment() {
        kafkaContainer.stop();
        kafkaContainer.close();
    }

    @Test
    void testCHKafkaProducerBeanCorrectlyIsConfigured() {
        assertNotNull(chKafkaProducer);
    }
}
