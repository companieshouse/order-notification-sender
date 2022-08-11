package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.ordernotification.config.TestConfig;
import uk.gov.companieshouse.ordernotification.emailsender.NonRetryableFailureException;

@SpringBootTest
@Import(TestConfig.class)
@TestPropertySource(locations="classpath:application-stubbed.properties")
@ExtendWith(MockitoExtension.class)
class SummaryEmailDataDirectorTest {

    @Autowired
    private SummaryEmailDataDirector summaryEmailDataDirector;

    @MockBean
    private OrderNotificationDataConvertable orderNotificationDataConvertable;

    @Mock
    private OrdersApi ordersApi;

    @Mock
    private BaseItemApi certificate;

    @Mock
    private BaseItemApi certifiedCopy;

    @Mock
    private BaseItemApi missingImageDelivery;

    @Mock
    private OrderNotificationEmailData emailData;

    @Test
    @DisplayName("test maps item kinds to the correct mapper methods")
    void testMapSuccess() {
        // given
        when(certificate.getKind()).thenReturn("item#certificate");
        when(certifiedCopy.getKind()).thenReturn("item#certified-copy");
        when(missingImageDelivery.getKind()).thenReturn("item#missing-image-delivery");
        when(ordersApi.getItems()).thenReturn(Arrays.asList(certificate, certifiedCopy, missingImageDelivery));
        when(orderNotificationDataConvertable.getEmailData()).thenReturn(emailData);

        // when
        OrderNotificationEmailData actual = summaryEmailDataDirector.map(ordersApi);

        // then
        assertEquals(emailData, actual);
        verify(orderNotificationDataConvertable).mapOrder(ordersApi);
        verify(orderNotificationDataConvertable).mapCertificate(certificate);
        verify(orderNotificationDataConvertable).mapCertifiedCopy(certifiedCopy);
        verify(orderNotificationDataConvertable).mapMissingImageDelivery(missingImageDelivery);
    }

    @Test
    @DisplayName("test throws non-retryable exception when handling an unknown item kind")
    void testMapFailure() {
        // given
        when(certificate.getKind()).thenReturn("incorrect-kind");
        when(ordersApi.getItems()).thenReturn(Collections.singletonList(certificate));

        // when
        Executable actual = () -> summaryEmailDataDirector.map(ordersApi);

        // then
        NonRetryableFailureException exception = assertThrows(NonRetryableFailureException.class, actual);
        assertEquals("Unhandled kind: [incorrect-kind]", exception.getMessage());
        verify(orderNotificationDataConvertable).mapOrder(ordersApi);
        verifyNoMoreInteractions(orderNotificationDataConvertable);
    }
}
