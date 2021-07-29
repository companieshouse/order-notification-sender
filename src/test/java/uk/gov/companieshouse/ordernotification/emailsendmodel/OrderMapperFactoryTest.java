package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryApi;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class OrderMapperFactoryTest {

    @InjectMocks
    OrderMapperFactory mapperFactory;

    @Mock
    CertificateOrderNotificationMapper certificateOrderNotificationMapper;

    @Mock
    DocumentOrderNotificationMapper documentOrderNotificationMapper;

    @Mock
    MissingImageOrderNotificationMapper missingImageOrderNotificationMapper;

    @Test
    void returnCertificateMapperIfClassCertificateApi() {
        // given
        OrdersApi ordersApi = new OrdersApi();
        ordersApi.setItems(Collections.singletonList(new CertificateApi()));

        // when
        OrdersApiMapper ordersApiMapper = mapperFactory.getOrderMapper(ordersApi);

        // then
        assertTrue(ordersApiMapper instanceof CertificateOrderNotificationMapper);
    }

    @Test
    void returnDocumentOrderMapperIfClassCertifiedCopyApi() {
        // given
        OrdersApi ordersApi = new OrdersApi();
        ordersApi.setItems(Collections.singletonList(new CertifiedCopyApi()));

        // when
        OrdersApiMapper ordersApiMapper = mapperFactory.getOrderMapper(ordersApi);

        // then
        assertTrue(ordersApiMapper instanceof DocumentOrderNotificationMapper);
    }

    @Test
    void returnMissingImageMapperIfClassMissingImageDeliveryApi() {
        // given
        OrdersApi ordersApi = new OrdersApi();
        ordersApi.setItems(Collections.singletonList(new MissingImageDeliveryApi()));

        // when
        OrdersApiMapper ordersApiMapper = mapperFactory.getOrderMapper(ordersApi);

        // then
        assertTrue(ordersApiMapper instanceof MissingImageOrderNotificationMapper);
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
