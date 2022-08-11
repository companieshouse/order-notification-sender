package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.config.TestConfig;

@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(locations="classpath:application-stubbed.properties")
@ExtendWith(MockitoExtension.class)
class OrderNotificationEmailDataConverterTest {

    @Autowired
    private OrderNotificationDataConvertable converter;

    @Mock
    private OrdersApi ordersApi;

    @Test
    void testMapOrder() {
        // given
        when(ordersApi.getReference()).thenReturn("orderRef");
        // when
        converter.mapOrder(ordersApi);
        // then
        assertEquals("orderRef", converter.getEmailData().getOrderId());
        assertEquals("/GCI-2224/TODO", converter.getEmailData().getOrderSummaryLink());
    }
}
