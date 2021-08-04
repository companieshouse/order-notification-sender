package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OrderMapperFactoryTest {

    private OrderMapperFactory mapperFactory;

    @Mock
    private OrdersApiMapper orderMapper;

    @Mock
    private BaseItemApi item;

    @BeforeEach
    void setup() {
        this.mapperFactory = new OrderMapperFactory(Collections.singletonMap("mapper", orderMapper));
    }

    @Test
    void returnCertificateMapperIfClassCertificateApi() {
        // when
        OrdersApiMapper ordersApiMapper = mapperFactory.getOrderMapper("mapper");

        // then
        assertNotNull(ordersApiMapper);
    }

    @Test
    void throwsIllegalArgumentExceptionIfClassIsInvalid() {
        // when
        Executable actual = () -> mapperFactory.getOrderMapper("not_mapper");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Unhandled item kind", exception.getMessage());
    }
}
