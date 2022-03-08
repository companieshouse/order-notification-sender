package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@ExtendWith(MockitoExtension.class)
class OrdersApiDetailsCommonFieldsMapperTest {

    @Mock
    private EmailConfiguration emailConfiguration;

    @InjectMocks
    private OrdersApiDetailsCommonFieldsMapper ordersApiDetailsCommonFieldsMapper;

    @Mock
    private OrdersApiDetails ordersApiDetails;

    @Test
    void testMapCommonFieldsMapsSuccessfully() {
        // given
        OrderModel orderModel = new OrderModel();

        when(ordersApiDetails.getOrderEmail()).thenReturn("demo@ch.gov.uk");
        when(ordersApiDetails.getCompanyName()).thenReturn("company name");
        when(ordersApiDetails.getCompanyNumber()).thenReturn("12345678");
        when(ordersApiDetails.getOrderReference()).thenReturn("reference");
        when(ordersApiDetails.getTotalOrderCost()).thenReturn("15");
        when(ordersApiDetails.getPaymentReference()).thenReturn("pay ref");
        when(ordersApiDetails.getOrderedAt()).thenReturn(LocalDateTime.parse("04/03/22 12:00:00",
                DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss")));

        when(emailConfiguration.getConfirmationMessage()).thenReturn("confirmed {0}");
        when(emailConfiguration.getPaymentDateFormat()).thenReturn("dd/MM/yy");

        // when
        ordersApiDetailsCommonFieldsMapper.mapCommonFields(orderModel, ordersApiDetails);

        // then
        assertEquals("demo@ch.gov.uk", orderModel.getTo());
        assertEquals("confirmed reference", orderModel.getSubject());
        assertEquals("company name", orderModel.getCompanyName());
        assertEquals("12345678", orderModel.getCompanyNumber());
        assertEquals("reference", orderModel.getOrderReferenceNumber());
        assertEquals("£15", orderModel.getAmountPaid());
        assertEquals("pay ref", orderModel.getPaymentReference());
        assertEquals("04/03/22", orderModel.getPaymentTime());
    }
}
