package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@ExtendWith(MockitoExtension.class)
class OrdersApiDetailsMapperTest {
    @Mock
    private DateGenerator dateGenerator;

    @Mock
    private EmailConfiguration emailConfiguration;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private OrderKindMapperFactory orderKindMapperFactory;

    @InjectMocks
    private OrdersApiDetailsMapper ordersApiDetailsMapper;

    @Mock
    private OrdersApiDetails ordersApiDetails;

    @Mock
    private OrderKindMapper kindMapper;

    @Mock
    private OrderDetails orderDetails;

    @Mock
    private OrderModel orderModel;

    @Mock
    private JsonProcessingException jsonProcessingException;

    @Test
    void testSuccessfullyMapsOrdersApiToEmailSendObject() throws JsonProcessingException {
        // given
        when(ordersApiDetails.getKind()).thenReturn("item#certificate");
        when(orderKindMapperFactory.getInstance("item#certificate")).thenReturn(kindMapper);
        when(kindMapper.map(ordersApiDetails)).thenReturn(orderDetails);
        when(emailConfiguration.getSenderAddress()).thenReturn("address");
        when(objectMapper.writeValueAsString(orderModel)).thenReturn("json string");
        when(orderDetails.getOrderModel()).thenReturn(orderModel);
        when(orderDetails.getMessageId()).thenReturn("certificate");
        when(emailConfiguration.getApplicationId()).thenReturn("order-notification-sender");
        when(orderDetails.getMessageType()).thenReturn("certificate");
        when(emailConfiguration.getDateFormat()).thenReturn("dd/MM/yy");
        when(dateGenerator.generate()).thenReturn(LocalDateTime.parse("04/03/22 12:00:00",
                DateTimeFormatter.ofPattern("dd/MM/yy HH:mm:ss")));

        // when
        EmailSend actual = ordersApiDetailsMapper.mapToEmailSend(ordersApiDetails);

        // then
        assertEquals("address", actual.getEmailAddress());
        assertEquals("json string", actual.getData());
        assertEquals("certificate", actual.getMessageId());
        assertEquals("order-notification-sender", actual.getAppId());
        assertEquals("certificate", actual.getMessageType());
        assertEquals("04/03/22", actual.getCreatedAt());
    }

    @Test
    @DisplayName("Should throw MappingException when email data cannot be serialised")
    void testShouldThrowMappingExceptionWhenObjectMapperThrowsJSonProcessingException() throws JsonProcessingException {
        //given
        when(ordersApiDetails.getKind()).thenReturn("item#certificate");
        when(ordersApiDetails.getOrderReference()).thenReturn("123456");
        when(orderKindMapperFactory.getInstance("item#certificate")).thenReturn(kindMapper);
        when(kindMapper.map(ordersApiDetails)).thenReturn(orderDetails);
        when(emailConfiguration.getSenderAddress()).thenReturn("address");
        when(orderDetails.getOrderModel()).thenReturn(orderModel);
        when(objectMapper.writeValueAsString(orderModel)).thenThrow(jsonProcessingException);
        //then
        Executable actual = () -> ordersApiDetailsMapper.mapToEmailSend(ordersApiDetails);

        //when
        Exception exception = assertThrows(MappingException.class, actual);
        assertEquals("Failed to map orderDetails: 123456", exception.getMessage());
    }
}
