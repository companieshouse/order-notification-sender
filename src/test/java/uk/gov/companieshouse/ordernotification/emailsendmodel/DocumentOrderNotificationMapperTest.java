package uk.gov.companieshouse.ordernotification.emailsendmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.ActionedByApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryMethodApi;
import uk.gov.companieshouse.api.model.order.item.FilingHistoryDocumentApi;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentOrderNotificationMapperTest {

    private DocumentOrderNotificationMapper documentOrderNotificationMapper;

    @Mock
    private DateGenerator dateGenerator;

    @Mock
    private FilingHistoryDescriptionProviderService providerService;

    @Mock
    private DeliveryMethodMapper deliveryMethodMapper;


    @BeforeEach
    void setup() {
        documentOrderNotificationMapper = new DocumentOrderNotificationMapper(dateGenerator,
                TestConstants.EMAIL_DATE_FORMAT, TestConstants.SENDER_EMAIL_ADDRESS, TestConstants.PAYMENT_DATE_FORMAT,
                TestConstants.MESSAGE_ID, TestConstants.APPLICATION_ID, TestConstants.MESSAGE_TYPE,
                TestConstants.CONFIRMATION_MESSAGE, providerService, new ObjectMapper(), deliveryMethodMapper);
    }

    @Test
    void testMapCertifiedDocument() throws JsonProcessingException {
        // given
        OrdersApi order = getOrder();
        when(dateGenerator.generate()).thenReturn(LocalDateTime.of(2021, 7, 27, 15, 20, 10));
        when(providerService.mapFilingHistoryDescription(eq(TestConstants.FILING_HISTORY_DESCRIPTION), any())).thenReturn(TestConstants.MAPPED_FILING_HISTORY_DESCRIPTION);
        when(deliveryMethodMapper.mapDeliveryMethod(any(), any())).thenReturn(TestConstants.DELIVERY_METHOD);

        // when
        EmailSend result = documentOrderNotificationMapper.map(order);

        // then
        assertEquals(getExpectedEmailSendModel(), result);
        verify(providerService).mapFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION, getFilingHistoryDescriptionValues());
    }

    private EmailSend getExpectedEmailSendModel() throws JsonProcessingException {
        EmailSend expected = new EmailSend();
        expected.setAppId(TestConstants.APPLICATION_ID);
        OrderModel model = getExpectedModel();
        model.setTo(TestConstants.EMAIL_RECIPIENT);
        model.setSubject(MessageFormat.format(TestConstants.CONFIRMATION_MESSAGE, TestConstants.ORDER_REFERENCE_NUMBER));
        model.setOrderReferenceNumber(TestConstants.ORDER_REFERENCE_NUMBER);
        model.setPaymentReference(TestConstants.PAYMENT_REFERENCE);
        model.setPaymentTime(TestConstants.PAYMENT_TIME);
        model.setAmountPaid(TestConstants.ORDER_VIEW);
        expected.setData(new ObjectMapper().writeValueAsString(model));
        expected.setCreatedAt(TestConstants.ORDER_CREATED_AT);
        expected.setEmailAddress(TestConstants.SENDER_EMAIL_ADDRESS);
        expected.setMessageId(TestConstants.MESSAGE_ID);
        expected.setMessageType(TestConstants.MESSAGE_TYPE);
        return expected;
    }

    private DocumentOrderNotificationModel getExpectedModel() {
        DocumentOrderNotificationModel expected = new DocumentOrderNotificationModel();
        expected.setCompanyName(TestConstants.COMPANY_NAME);
        expected.setCompanyNumber(TestConstants.COMPANY_NUMBER);
        expected.setDeliveryMethod(TestConstants.DELIVERY_METHOD);

        FilingHistoryDetailsModel details = new FilingHistoryDetailsModel();
        details.setFilingHistoryCost(TestConstants.ORDER_COST);
        details.setFilingHistoryDate(TestConstants.FILING_HISTORY_DATE);
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
