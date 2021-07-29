package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.CertificateApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyApi;

import javax.print.Doc;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class OrderMapperFactoryTest {

    @InjectMocks
    OrderMapperFactory mapperFactory;

    @Mock
    CertificateOrderNotificationMapper certificateOrderNotificationMapper;

    @Mock
    DocumentOrderNotificationMapper documentOrderNotificationMapper;

    @Test
    void returnCertificateMapperIfItemKindCertificate() {
        // given
        OrdersApi ordersApi = new OrdersApi();
        CertificateApi certificateApi = new CertificateApi();
        certificateApi.setKind("item#certificate");
        ordersApi.setItems(Collections.singletonList(certificateApi));

        // when
        OrdersApiMapper ordersApiMapper = mapperFactory.getOrderMapper(ordersApi);

        // then
        assertTrue(ordersApiMapper instanceof CertificateOrderNotificationMapper);
    }

    @Test
    void returnDocumentOrderMapperIfItemKindCertificate() {
        // given
        OrdersApi ordersApi = new OrdersApi();
        CertifiedCopyApi certifiedCopyApi = new CertifiedCopyApi();
        certifiedCopyApi.setKind("item#certified-copy");
        ordersApi.setItems(Collections.singletonList(certifiedCopyApi));

        // when
        OrdersApiMapper ordersApiMapper = mapperFactory.getOrderMapper(ordersApi);

        // then
        assertTrue(ordersApiMapper instanceof DocumentOrderNotificationMapper);
    }

}
