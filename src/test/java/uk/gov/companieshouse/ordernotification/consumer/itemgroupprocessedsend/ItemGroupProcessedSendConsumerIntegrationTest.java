package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;


import java.util.concurrent.CountDownLatch;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;

import email.email_send;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.ordernotification.config.TestConfig;
import uk.gov.companieshouse.ordernotification.config.TestEnvironmentSetupHelper;

@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(locations = "classpath:application-stubbed.properties")
class ItemGroupProcessedSendConsumerIntegrationTest {

    private static WireMockServer wireMockServer;

    @Autowired
    private KafkaProducer<String, ItemGroupProcessedSend> itemGroupProcessedSendProducer;

    @Autowired
    private KafkaConsumer<String, email_send> emailSendConsumer;

    @Autowired
    private ObjectMapper objectMapper;

    private CountDownLatch eventLatch;

    @BeforeAll
    static void before() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        TestEnvironmentSetupHelper.setEnvironmentVariable("API_URL",
            "http://localhost:" + wireMockServer.port());
        TestEnvironmentSetupHelper.setEnvironmentVariable("CHS_API_KEY", "123");
        TestEnvironmentSetupHelper.setEnvironmentVariable("PAYMENTS_API_URL",
            "http://localhost:" + wireMockServer.port());
    }

    @AfterAll
    static void after() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setup() {
        eventLatch = new CountDownLatch(1);
        ItemGroupProcessedSendKafkaConsumerAspect.setEventLatch(eventLatch);
        wireMockServer.resetAll();
    }

    @AfterEach
    void teardown() {
        wireMockServer.resetAll();
    }
}
