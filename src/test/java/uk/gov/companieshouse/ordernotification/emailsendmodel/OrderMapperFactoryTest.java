package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class OrderMapperFactoryTest {

    private OrderMapperFactory mapperFactory;

    @Mock
    private OrdersApiMapper orderMapper;

    @Mock
    private BaseItemApi item;

    @BeforeEach
    void setup() {
        this.mapperFactory = new OrderMapperFactory(Collections.singletonMap(BaseItemApi.class, orderMapper));
    }

    @Test
    void returnCertificateMapperIfClassCertificateApi() {
        // given
        OrdersApi ordersApi = new OrdersApi();
        ordersApi.setItems(Collections.singletonList(item));

        // when
        OrdersApiMapper ordersApiMapper = mapperFactory.getOrderMapper(ordersApi);

        // then
        assertNotNull(ordersApiMapper);
    }

    @Test
    void throwsIllegalArgumentExceptionIfClassIsInvalid() {
        // given
        OrdersApi ordersApi = new OrdersApi();
        ordersApi.setItems(Collections.singletonList(new BaseItemApi(){}));

        // when
        Executable actual = () -> mapperFactory.getOrderMapper(ordersApi);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, actual);
        assertEquals("Unhandled item class", exception.getMessage());
    }
}
