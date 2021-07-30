package uk.gov.companieshouse.ordernotification.ordersprocessor;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.emailmodel.OrderResourceOrderNotificationEnricher;
import uk.gov.companieshouse.ordernotification.fixtures.TestUtils;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiService;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersServiceException;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/** Integration tests the {@link OrderResourceOrderNotificationEnricher} service. */
// TODO: Create email sender
@SpringBootTest
@EmbeddedKafka
@DirtiesContext
@TestPropertySource(locations = "classpath:application-stubbed.properties")
@Disabled("Subject to rework using Spring events")
class OrderResourceOrderEnricherIntegrationTest {

    @Autowired
    private OrderResourceOrderNotificationEnricher orderResourceOrderEnricherUnderTest;

    @MockBean
    private OrdersApiService ordersApi;

    @MockBean
    private Consumer consumer;

    @Mock
    private LoggingUtils loggingUtils;

    @Test
    @DisplayName("processOrderReceived() propagates non-retryable ServiceException so consumer can handle it accordingly")
    void propagatesNonRetryableServiceException() throws Exception {

        // Given we have an order that somehow contains no items (invalid input)
        final OrdersApi order = new OrdersApi();
        order.setReference(TestUtils.ORDER_REFERENCE);
        when(ordersApi.getOrderData(anyString())).thenReturn(order);

        // When and then
        assertThatExceptionOfType(OrdersServiceException.class).isThrownBy(() ->
            orderResourceOrderEnricherUnderTest.enrich(TestUtils.ORDER_RECEIVED_URI))
            .withMessage("Order ORD-432118-793830 contains no items.")
            .withNoCause();
    }
}
