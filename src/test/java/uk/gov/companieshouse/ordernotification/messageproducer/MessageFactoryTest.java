package uk.gov.companieshouse.ordernotification.messageproducer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka
@TestPropertySource(locations = "classpath:application-stubbed-empty.properties")
class MessageFactoryTest {
    @Autowired
    private SerializerFactory serializerFactory;
    @Autowired
    private DeserializerFactory deserializerFactory;
    @Autowired
    private LoggingUtils loggingUtils;

    private static KafkaContainer kafkaContainer;

    @BeforeAll
    static void before() {
        kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));
        kafkaContainer.start();
        System.setProperty("spring.kafka.bootstrap-servers", kafkaContainer.getBootstrapServers());
    }

    @AfterAll
    static void after() {
        kafkaContainer.stop();
    }

    @Test
    void createMessageSuccessfullyCreatesMessage() throws Exception {
        // Given
        MessageFactory messageFactory = new MessageFactory(serializerFactory, loggingUtils);

        // When
        Message message = messageFactory.createMessage(createEmailSend(), TestConstants.ORDER_REFERENCE_NUMBER, TestConstants.TOPIC);
        String actualContent = new String(message.getValue());

        // Then
        AvroSerializer<EmailSend> serializer = serializerFactory.getGenericRecordSerializer(EmailSend.class);
        String expectedContent = new String(serializer.toBinary(createEmailSend()));
        Assertions.assertEquals(expectedContent, actualContent);
        Assertions.assertEquals(TestConstants.TOPIC, message.getTopic());
    }

    private EmailSend createEmailSend() {
        EmailSend emailSend = new EmailSend();
        emailSend.setAppId(TestConstants.APPLICATION_ID);
        emailSend.setData(TestConstants.EMAIL_DATA);
        emailSend.setEmailAddress(TestConstants.SENDER_EMAIL_ADDRESS);
        emailSend.setMessageId(TestConstants.MESSAGE_ID);
        emailSend.setMessageType(TestConstants.MESSAGE_TYPE);
        emailSend.setCreatedAt(TestConstants.CREATED_AT);

        return emailSend;
    }
}
