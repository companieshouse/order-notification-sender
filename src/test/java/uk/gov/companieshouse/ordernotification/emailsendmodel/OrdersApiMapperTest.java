package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;

@ExtendWith(MockitoExtension.class)
class OrdersApiMapperTest {
    @Mock
    private DateGenerator dateGenerator;

    @Mock
    private EmailConfiguration emailConfiguration;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private OrdersApiDetailsMapper ordersApiMapper;

    @Test
    @DisplayName("Should throw MappingException when email data cannot be serialised")
    void testShouldThrowMappingExceptionWhenObjectMapperThrowsJSonProcessingException() {
        //given
        // TODO when(emailConfiguration.getSenderAddress()).thenReturn("address");

        //then
        //when
    }
}
