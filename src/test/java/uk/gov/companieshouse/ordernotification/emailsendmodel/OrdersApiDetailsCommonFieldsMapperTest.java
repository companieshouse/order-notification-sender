package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.ActionedByApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@ExtendWith(MockitoExtension.class)
public class OrdersApiDetailsCommonFieldsMapperTest {

    @Mock
    private EmailConfiguration emailConfiguration;

    @InjectMocks
    private OrdersApiDetailsCommonFieldsMapper ordersApiDetailsCommonFieldsMapper;

    @Mock
    private OrdersApiDetails ordersApiDetails;

    @Mock
    private OrdersApi ordersApi;

    @Mock
    private ActionedByApi actionedByApi;

    @Mock
    private BaseItemApi baseItemApi;

    @Test
    void testMapCommonFieldsMapsSuccessfully() {
        // given
        OrderModel orderModel = new OrderModel();

        when(ordersApiDetails.getOrdersApi()).thenReturn(ordersApi);
        when(ordersApi.getOrderedBy()).thenReturn(actionedByApi);
        when(actionedByApi.getEmail()).thenReturn("demo@ch.gov.uk");
        when(ordersApi.getReference()).thenReturn("reference");
        when(emailConfiguration.getConfirmationMessage()).thenReturn("confirmed %s");
        when(ordersApiDetails.getBaseItemApi()).thenReturn(baseItemApi);
        when(baseItemApi.getCompanyName()).thenReturn("company name");
        when(baseItemApi.getCompanyNumber()).thenReturn("12345678");
        when(ordersApi.getTotalOrderCost()).thenReturn("15");
        when(ordersApi.getPaymentReference()).thenReturn("pay ref");
        when(emailConfiguration.getPaymentDateFormat()).thenReturn("dd/MM/yy");
        when(ordersApi.getOrderedAt()).thenReturn(LocalDateTime.parse("04/03/22 12:00:00",
                DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss")));

        // when
        ordersApiDetailsCommonFieldsMapper.mapCommonFields(orderModel, ordersApiDetails);

        // then
        assertEquals("demo@ch.gov.uk", orderModel.getTo());
        //assertEquals("confirmed reference", orderModel.getSubject());
        assertEquals("company name", orderModel.getCompanyName());
        assertEquals("12345678", orderModel.getCompanyNumber());
        assertEquals("reference", orderModel.getOrderReferenceNumber());
        assertEquals("£15", orderModel.getAmountPaid());
        assertEquals("pay ref", orderModel.getPaymentReference());
        assertEquals("04/03/22", orderModel.getPaymentTime());
    }
}
