package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.ActionedByApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryMethodApi;
import uk.gov.companieshouse.api.model.order.item.FilingHistoryDocumentApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@ExtendWith(MockitoExtension.class)
class DocumentOrderDetailsMapperTest {

    @Mock
    private EmailConfiguration config;

    @Mock
    private DeliveryMethodMapper deliveryMethodMapper;

    @Mock
    private FilingHistoryDescriptionProviderService providerService;

    @Mock
    private OrdersApiDetailsCommonFieldsMapper commonFieldsMapper;

    @InjectMocks
    private DocumentOrderDetailsMapper documentOrderDetailsMapper;

    @Mock
    private EmailDataConfiguration emailDataConfig;

    @Mock
    private OrdersApiDetails ordersApiDetails;

    @Test
    void testMapCertifiedDocument() {
        // given
        OrdersApi order = getOrder();
        when(ordersApiDetails.getBaseItemOptions()).thenReturn(order.getItems().get(0).getItemOptions());
        when(providerService.mapFilingHistoryDescription(eq(TestConstants.FILING_HISTORY_DESCRIPTION), any())).thenReturn(TestConstants.MAPPED_FILING_HISTORY_DESCRIPTION);
        when(deliveryMethodMapper.mapDeliveryMethod(any(), any())).thenReturn(TestConstants.DELIVERY_METHOD);

        when(config.getDocument()).thenReturn(emailDataConfig);
        when(emailDataConfig.getFilingHistoryDateFormat()).thenReturn(TestConstants.EMAIL_DATE_FORMAT);

        // when
        DocumentOrderNotificationModel result = documentOrderDetailsMapper.map(ordersApiDetails);

        // then
        assertEquals(getExpectedModel(), result);
        verify(providerService).mapFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION, getFilingHistoryDescriptionValues());
    }

    private DocumentOrderNotificationModel getExpectedModel() {
        DocumentOrderNotificationModel expected = new DocumentOrderNotificationModel();
        expected.setDeliveryMethod(TestConstants.DELIVERY_METHOD);

        FilingHistoryDetailsModel details = new FilingHistoryDetailsModel();
        details.setFilingHistoryCost(TestConstants.ORDER_VIEW);
        details.setFilingHistoryDate(TestConstants.FILING_HISTORY_DATE_VIEW);
        details.setFilingHistoryDescription(TestConstants.MAPPED_FILING_HISTORY_DESCRIPTION);
        details.setFilingHistoryType(TestConstants.FILING_HISTORY_TYPE);

        expected.setFilingHistoryDocuments(Collections.singletonList(details));

        return expected;
    }

    private OrdersApi getOrder() {
        OrdersApi order = new OrdersApi();
        order.setReference(TestConstants.ORDER_REFERENCE_NUMBER);
        ActionedByApi orderedBy = new ActionedByApi();
        orderedBy.setEmail(TestConstants.EMAIL_RECIPIENT);
        order.setOrderedBy(orderedBy);

        CertifiedCopyApi item = new CertifiedCopyApi();
        item.setCompanyName(TestConstants.COMPANY_NAME);
        item.setCompanyNumber(TestConstants.COMPANY_NUMBER);

        CertifiedCopyItemOptionsApi itemOptions = new CertifiedCopyItemOptionsApi();
        itemOptions.setDeliveryMethod(DeliveryMethodApi.POSTAL);

        FilingHistoryDocumentApi filingHistoryDocumentApi = new FilingHistoryDocumentApi();
        filingHistoryDocumentApi.setFilingHistoryCost(TestConstants.ORDER_COST);
        filingHistoryDocumentApi.setFilingHistoryDate(TestConstants.FILING_HISTORY_DATE);
        filingHistoryDocumentApi.setFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION);
        filingHistoryDocumentApi.setFilingHistoryDescriptionValues(getFilingHistoryDescriptionValues());
        filingHistoryDocumentApi.setFilingHistoryType(TestConstants.FILING_HISTORY_TYPE);
        itemOptions.setFilingHistoryDocuments(Collections.singletonList(filingHistoryDocumentApi));
        item.setItemOptions(itemOptions);

        order.setItems(Collections.singletonList(item));
        order.setTotalOrderCost(TestConstants.ORDER_COST);
        order.setPaymentReference(TestConstants.PAYMENT_REFERENCE);
        order.setOrderedAt(LocalDateTime.of(2021, 7, 27, 15,20,10));

        return order;
    }

    private Map<String, Object> getFilingHistoryDescriptionValues() {
        Map<String, Object> filingHistoryDescriptionValues = new HashMap<>();
        filingHistoryDescriptionValues.put("made_up_date", TestConstants.MADE_UP_DATE);
        filingHistoryDescriptionValues.put("key", "value");
        return filingHistoryDescriptionValues;
    }
}
