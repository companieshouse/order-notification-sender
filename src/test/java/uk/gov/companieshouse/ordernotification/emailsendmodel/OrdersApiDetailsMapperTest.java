package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiWrappable;

@ExtendWith(MockitoExtension.class)
public class OrdersApiDetailsMapperTest {

    @InjectMocks
    private OrdersApiDetailsMapper mapper;

    @Mock
    private OrderNotificationEmailDataBuilderFactory factory;

    @Mock
    private DateGenerator dateGenerator;

    @Mock
    private EmailConfiguration config;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private SummaryEmailDataDirector director;

    @Mock
    private OrderNotificationDataConvertable converter;

    @Mock
    private OrdersApiWrappable ordersApiWrapper;

    @Mock
    private OrdersApi ordersApi;

    @Mock
    private OrderNotificationEmailData emailData;

    @Test
    void testMapToEmailSendSuccess() throws JsonProcessingException {
        // given
        EmailSend expected = new EmailSend();
        expected.setEmailAddress("address");
        expected.setData("email data json");
        expected.setMessageId("id");
        expected.setAppId("id");
        expected.setMessageType("type");
        expected.setCreatedAt("10/08/2022");

        when(ordersApiWrapper.getOrdersApi()).thenReturn(ordersApi);
        when(config.getSenderAddress()).thenReturn("address");
        when(objectMapper.writeValueAsString(emailData)).thenReturn("email data json");
        when(config.getMessageId()).thenReturn("id");
        when(config.getApplicationId()).thenReturn("id");
        when(config.getMessageType()).thenReturn("type");
        when(dateGenerator.generate()).thenReturn(LocalDateTime.of(2022, 8, 10, 11, 42));
        when(config.getDateFormat()).thenReturn("dd/MM/yyyy");
        when(factory.newDirector(converter)).thenReturn(director);
        when(factory.newConverter()).thenReturn(converter);
        when(converter.getEmailData()).thenReturn(emailData);

        // when
        EmailSend actual = mapper.mapToEmailSend(ordersApiWrapper);

        // then
        assertEquals(expected, actual);
        verify(director).map(ordersApi);
    }

    @Test
    void testMapToEmailSendFailure() throws JsonProcessingException {
        // given
        when(ordersApiWrapper.getOrdersApi()).thenReturn(ordersApi);
        when(objectMapper.writeValueAsString(any())).thenThrow(JsonProcessingException.class);
        when(ordersApi.getReference()).thenReturn("12345");
        when(factory.newDirector(converter)).thenReturn(director);
        when(factory.newConverter()).thenReturn(converter);
        when(converter.getEmailData()).thenReturn(emailData);

        // when
        Executable actual = () -> mapper.mapToEmailSend(ordersApiWrapper);

        // then
        MappingException exception = assertThrows(MappingException.class, actual);
        assertEquals("Failed to map order: 12345", exception.getMessage());
        verify(objectMapper).writeValueAsString(emailData);
        verify(director).map(ordersApi);
    }
}
