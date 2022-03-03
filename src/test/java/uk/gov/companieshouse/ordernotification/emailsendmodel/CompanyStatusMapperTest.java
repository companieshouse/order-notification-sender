package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class CompanyStatusMapperTest {

    @Mock
    private StatusMappable statusMapper;

    @Mock
    private CertificateItemOptionsApi certificateItemOptionsApi;

    @Mock
    private CertificateOrderNotificationModel certificateOrderNotificationModel;

    @Test
    void testMapWithLookedUpCommandIfCompanyStatusHandled() {
        //given
        CompanyStatusMapper mapper = new CompanyStatusMapper(Collections.singletonMap("liquidation", statusMapper), null);
        when(certificateItemOptionsApi.getCompanyStatus()).thenReturn("liquidation");

        //when
        mapper.map(certificateItemOptionsApi, certificateOrderNotificationModel);

        //then
        verify(statusMapper).map(certificateItemOptionsApi, certificateOrderNotificationModel);
    }

    @Test
    void testMapWithDefaultCommandIfCompanyStatusUnhandled() {
        //given
        CompanyStatusMapper mapper = new CompanyStatusMapper(Collections.emptyMap(), statusMapper);
        when(certificateItemOptionsApi.getCompanyStatus()).thenReturn("active");

        //when
        mapper.map(certificateItemOptionsApi, certificateOrderNotificationModel);

        //then
        verify(statusMapper).map(certificateItemOptionsApi, certificateOrderNotificationModel);
    }
}