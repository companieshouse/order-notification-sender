package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static uk.gov.companieshouse.ordernotification.TestUtils.noOfRecordsForTopic;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.ITEM_GROUP_PROCESSED_SEND;
import static uk.gov.companieshouse.ordernotification.fixtures.TestConstants.SAME_PARTITION_KEY;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.JsonBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.messaging.Message;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.utility.DockerImageName;
import uk.gov.companieshouse.itemgroupprocessedsend.ItemGroupProcessedSend;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.ordernotification.config.EmailSendSendTestConfig;
import uk.gov.companieshouse.ordernotification.config.TestEnvironmentSetupHelper;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSendService;
import uk.gov.companieshouse.ordernotification.emailsender.SendItemReadyEmailEvent;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.messageproducer.MessageProducer;

/**
 * These tests verify that exceptions originated in
 * {@link EmailSendService#handleEvent(SendItemReadyEmailEvent)} are handled correctly and either
 * result in the processing of the incoming {@link ItemGroupProcessedSend}
 * being retried up to the maximum configured number of retries, or not, as the case
 * may be.
 */
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@EmbeddedKafka(
    topics = {"${kafka.topics.item-group-processed-send}",
        "${kafka.topics.item-group-processed-send}-retry",
        "${kafka.topics.item-group-processed-send}-error",
        "${kafka.topics.item-group-processed-send}-invalid"},
    controlledShutdown = true,
    partitions = 1
)
@TestPropertySource(locations = "classpath:application-main-retryable.properties")
@Import(EmailSendSendTestConfig.class)
class ItemGroupProcessedSendConsumerEmailSendExceptionTest {

    private static MockServerContainer container;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private KafkaConsumer<String, ItemGroupProcessedSend> testConsumer;

    @Autowired
    private KafkaProducer<String, ItemGroupProcessedSend> testProducer;

    private MockServerClient client;

    @Autowired
    private CountDownLatch latch;

    @Autowired
    @Value("${kafka.topics.item-group-processed-send}")
    private String inboundTopic;

    @Autowired
    @Value("${kafka.topics.email-send}")
    private String outboundTopic;

    @SpyBean
    private ItemGroupProcessedSendHandler itemGroupProcessedSendHandler;

    @MockBean // TODO DCAC-295 SpyBean or MockBean?
    private MessageProducer messageProducer;

    @Captor
    private ArgumentCaptor<Message<ItemGroupProcessedSend>> messageCaptor;

    @BeforeAll
    static void before() {
        container = new MockServerContainer(DockerImageName.parse(
            "jamesdbloom/mockserver:mockserver-5.5.4"));
        container.start();
        TestEnvironmentSetupHelper.setEnvironmentVariable("API_URL",
            "http://" + container.getHost() + ":" + container.getServerPort());
        TestEnvironmentSetupHelper.setEnvironmentVariable("CHS_API_KEY", "123");
        TestEnvironmentSetupHelper.setEnvironmentVariable("PAYMENTS_API_URL",
            "http://" + container.getHost() + ":" + container.getServerPort());
    }

    @AfterAll
    static void after() {
        container.stop();
    }

    @BeforeEach
    void setup() {
        client = new MockServerClient(container.getHost(), container.getServerPort());
        latch = new CountDownLatch(1);
        ItemGroupProcessedSendKafkaConsumerAspect.setEventLatch(latch);
    }

    @AfterEach
    void teardown() {
        client.reset();
    }

    @Test
    void testTimeoutExceptionInEmailSendIsRetried()
        throws InterruptedException, SerializationException, ExecutionException, TimeoutException, IOException {
        verifyExceptionInEmailSendIsRetried(TimeoutException.class);
    }

    @Test
    void testExecutionExceptionInEmailSendIsRetried()
        throws InterruptedException, SerializationException, ExecutionException, TimeoutException, IOException {
        verifyExceptionInEmailSendIsRetried(ExecutionException.class);
    }

    @Test
    void testSerializationExceptionInEmailSendIsNotRetried()
        throws InterruptedException, SerializationException, ExecutionException, TimeoutException, IOException {
        verifyExceptionInEmailSendIsNotRetried(SerializationException.class);
    }

    @Test
    void testInterruptedExceptionInEmailSendIsNotRetried()
        throws InterruptedException, SerializationException, ExecutionException, TimeoutException, IOException {
        verifyExceptionInEmailSendIsNotRetried(InterruptedException.class);
    }

    private void verifyExceptionInEmailSendIsRetried(final Class<? extends Exception> exceptionClass)
        throws IOException, SerializationException, ExecutionException, InterruptedException, TimeoutException {

        // given
        client.when(request()
                .withPath(TestConstants.ORDER_NOTIFICATION_REFERENCE)
                .withMethod(HttpMethod.GET.toString()))
            .respond(response()
                .withStatusCode(HttpStatus.OK.value())
                .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .withBody(JsonBody.json(IOUtils.resourceToString(
                    "/fixtures/digital-certified-copy.json",
                    StandardCharsets.UTF_8))));
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(testConsumer);
        doThrow(exceptionClass).when(messageProducer)
            .sendMessage(any(GenericRecord.class), anyString(), eq(outboundTopic));

        // when
        testProducer.send(new ProducerRecord<>(
            inboundTopic, 0, System.currentTimeMillis(), SAME_PARTITION_KEY,
            ITEM_GROUP_PROCESSED_SEND));
        if (!latch.await(15L, TimeUnit.SECONDS)) {
            fail("Timed out waiting for latch");
        }

        // then
        final ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofMillis(15000L), 6);
        assertThat(noOfRecordsForTopic(consumerRecords, inboundTopic), is(1));
        assertThat(noOfRecordsForTopic(consumerRecords, inboundTopic + "-retry"), is(3));
        assertThat(noOfRecordsForTopic(consumerRecords, inboundTopic + "-error"), is(1));
        assertThat(noOfRecordsForTopic(consumerRecords, inboundTopic + "-invalid"), is(0));
        verify(itemGroupProcessedSendHandler, times(4)).handleMessage(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getPayload(), is(ITEM_GROUP_PROCESSED_SEND));

        verify(messageProducer, times(4))
            .sendMessage(any(GenericRecord.class), anyString(), eq(outboundTopic));
    }

    private void verifyExceptionInEmailSendIsNotRetried(final Class<? extends Exception> exceptionClass)
        throws IOException, SerializationException, ExecutionException, InterruptedException, TimeoutException {

        // given
        client.when(request()
                .withPath(TestConstants.ORDER_NOTIFICATION_REFERENCE)
                .withMethod(HttpMethod.GET.toString()))
            .respond(response()
                .withStatusCode(HttpStatus.OK.value())
                .withHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                .withBody(JsonBody.json(IOUtils.resourceToString(
                    "/fixtures/digital-certified-copy.json",
                    StandardCharsets.UTF_8))));
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(testConsumer);
        doThrow(exceptionClass).when(messageProducer)
            .sendMessage(any(GenericRecord.class), anyString(), eq(outboundTopic));

        // when
        testProducer.send(new ProducerRecord<>(
            inboundTopic, 0, System.currentTimeMillis(), SAME_PARTITION_KEY,
            ITEM_GROUP_PROCESSED_SEND));
        if (!latch.await(15L, TimeUnit.SECONDS)) {
            fail("Timed out waiting for latch");
        }

        // then
        final ConsumerRecords<?, ?> consumerRecords = KafkaTestUtils.getRecords(testConsumer, Duration.ofMillis(15000L), 2);
        assertThat(noOfRecordsForTopic(consumerRecords, inboundTopic), is(1));
        assertThat(noOfRecordsForTopic(consumerRecords, inboundTopic + "-retry"), is(0));
        assertThat(noOfRecordsForTopic(consumerRecords, inboundTopic + "-error"), is(0));
        assertThat(noOfRecordsForTopic(consumerRecords, inboundTopic + "-invalid"), is(1));
        verify(itemGroupProcessedSendHandler).handleMessage(messageCaptor.capture());
        assertThat(messageCaptor.getValue().getPayload(), is(ITEM_GROUP_PROCESSED_SEND));

        verify(messageProducer)
            .sendMessage(any(GenericRecord.class), anyString(), eq(outboundTopic));
    }
}


