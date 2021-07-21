package uk.gov.companieshouse.ordernotification.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.ordernotification.emailsender.service.ItemKafkaProducer;
import uk.gov.companieshouse.ordernotification.fixtures.TestUtils;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.ordersapi.model.OrderData;
import uk.gov.companieshouse.ordernotification.ordersapi.service.OrdersApiClientService;
import uk.gov.companieshouse.ordernotification.ordersapi.service.ServiceException;

import java.io.InterruptedIOException;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.ordernotification.fixtures.TestUtils.createOrder;

/** Integration tests the {@link OrderProcessorService} service. */
// TODO: Create email sender
@SpringBootTest
@EmbeddedKafka
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
class OrderProcessorServiceIntegrationTest {

    @Autowired
    private OrderProcessorService orderProcessorServiceUnderTest;

    @MockBean
    private OrdersApiClientService ordersApi;

    @MockBean
    private ItemKafkaProducer itemKafkaProducer;

    @MockBean
    private Consumer consumer;

    @Mock
    private LoggingUtils loggingUtils;

    @Test
    @DisplayName("processOrderReceived() propagates non-retryable ServiceException so consumer can handle it accordingly")
    void propagatesNonRetryableServiceException() throws Exception {

        // Given we have an order that somehow contains no items (invalid input)
        final OrderData order = new OrderData();
        order.setReference(TestUtils.ORDER_REFERENCE);
        when(ordersApi.getOrderData(anyString())).thenReturn(order);

        // When and then
        assertThatExceptionOfType(ServiceException.class).isThrownBy(() ->
            orderProcessorServiceUnderTest.processOrderReceived(TestUtils.ORDER_RECEIVED_URI))
            .withMessage("Order ORD-432118-793830 contains no items.")
            .withNoCause();
    }

    @Test
    @DisplayName("processOrderReceived() propagates non-retryable KafkaMessagingException so consumer can handle it accordingly")
    void propagatesNonRetryableKafkaMessagingException() throws Exception {

        // Given we have an order item that is missing a required field (invalid input - no item URI)
        final OrderData order = createOrder();
        order.getItems().get(0).setItemUri(null);
        when(ordersApi.getOrderData(anyString())).thenReturn(order);

        // When and then
        assertThatExceptionOfType(KafkaMessagingException.class).isThrownBy(() ->
            orderProcessorServiceUnderTest.processOrderReceived(TestUtils.ORDER_RECEIVED_URI))
            .withMessage("Unable to create message for order ORD-432118-793830 item ID MID-242116-007650!")
            .withCause(new NullPointerException(
                    "null of string in field item_uri of uk.gov.companieshouse.orders.items.Item in field item of " +
                            "uk.gov.companieshouse.orders.items.ChdItemOrdered"));
    }

    @Test
    @DisplayName("processOrderReceived() propagates retryable RetryableErrorException so consumer can retry")
    void propagatesRetryableErrorException() throws Exception {

        // Given we have a valid order...
        final OrderData order = createOrder();
        when(ordersApi.getOrderData(anyString())).thenReturn(order);

        // ...but something prevents successful production of the message to the topic
        final ExecutionException messageProductionError =
                new ExecutionException("Test generated exception.", new InterruptedIOException());
        doThrow(messageProductionError)
                .when(itemKafkaProducer).sendMessage(eq(TestUtils.ORDER_REFERENCE),
                                                     eq(TestUtils.MISSING_IMAGE_DELIVERY_ITEM_ID),
                                                     any(Message.class),
                                                     any(Consumer.class));

        // and when the order is processed then the message production error is propagated as a retryable error
        assertThatExceptionOfType(RetryableErrorException.class).isThrownBy(() ->
            orderProcessorServiceUnderTest.processOrderReceived(TestUtils.ORDER_RECEIVED_URI))
            .withMessage(
                "Kafka item message could not be sent for order reference ORD-432118-793830 item ID MID-242116-007650")
            .withCause(messageProductionError);
    }

}
