package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryMethodApi;
import uk.gov.companieshouse.api.model.order.item.FilingHistoryDocumentApi;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DocumentOrderNotificationMapperTest {

    private final String ORDER_REFERENCE_NUMBER = "87654321";
    private final String COMPANY_NAME = "ACME LTD";
    private final String COMPANY_NUMBER = "12345678";
    private final String ORDER_COST = "15";
    private final String PAYMENT_REFERENCE = "ABCD-EFGH-IJKL";
    private final String FILING_HISTORY_DATE = "2021-07-28";
    private final String DELIVERY_METHOD = "postal";
    private final String FILING_HISTORY_DESCRIPTION = "confirmation-statement-with-updates";
    private final String MADE_UP_DATE = "2017-05-20";
    private final String FILING_HISTORY_TYPE = "CS01";

    private final String MESSAGE_ID = "message_id";
    private final String APPLICATION_ID = "application_id";
    private final String MESSAGE_TYPE = "message_type";

    private DocumentOrderNotificationMapper documentOrderNotificationMapper;

    @Mock
    private DateGenerator dateGenerator;


    @BeforeEach
    void setup() {
        documentOrderNotificationMapper = new DocumentOrderNotificationMapper(dateGenerator, "dd MMMM yyyy", "noreply@companieshouse.gov.uk", "dd MMMM yyyy - HH:mm:ss", MESSAGE_ID, APPLICATION_ID, MESSAGE_TYPE);
    }

    @Test
    void testMapCertifiedDocument() throws JsonProcessingException {
        // given
        OrdersApi order = getOrder();
        when(dateGenerator.generate()).thenReturn(LocalDateTime.of(2021, 7, 27, 15, 20, 10));

        // when
        EmailSend result = documentOrderNotificationMapper.map(order);

        // then
        assertEquals(getExpectedEmailSendModel(), result);
    }

    private EmailSend getExpectedEmailSendModel() throws JsonProcessingException {
        EmailSend expected = new EmailSend();
        expected.setAppId(APPLICATION_ID);
        OrderModel model = getExpectedModel();
        model.setOrderReferenceNumber(ORDER_REFERENCE_NUMBER);
        model.setPaymentReference(PAYMENT_REFERENCE);
        model.setPaymentTime("27 July 2021 - 15:20:10");
        model.setTotalOrderCost(ORDER_COST);
        expected.setData(new ObjectMapper().writeValueAsString(model));
        expected.setCreatedAt("27 July 2021");
        expected.setEmailAddress("noreply@companieshouse.gov.uk");
        expected.setMessageId(MESSAGE_ID);
        expected.setMessageType(MESSAGE_TYPE);
        return expected;
    }

    private DocumentOrderNotificationModel getExpectedModel() {
        DocumentOrderNotificationModel expected = new DocumentOrderNotificationModel();
        expected.setCompanyName(COMPANY_NAME);
        expected.setCompanyNumber(COMPANY_NUMBER);
        expected.setDeliveryMethod(DELIVERY_METHOD);

        DocumentOrderDocumentDetailsModel details = new DocumentOrderDocumentDetailsModel();
        details.setFilingHistoryCost(ORDER_COST);
        details.setFilingHistoryDate(FILING_HISTORY_DATE);
        details.setFilingHistoryDescription(FILING_HISTORY_DESCRIPTION);
        details.setMadeUpDate(MADE_UP_DATE);
        details.setFilingHistoryType(FILING_HISTORY_TYPE);

        expected.setFilingHistoryDocuments(Collections.singletonList(details));

        return expected;
    }

    private OrdersApi getOrder() {
        OrdersApi order = new OrdersApi();
        order.setReference(ORDER_REFERENCE_NUMBER);

        CertifiedCopyApi item = new CertifiedCopyApi();
        item.setCompanyName(COMPANY_NAME);
        item.setCompanyNumber(COMPANY_NUMBER);

        CertifiedCopyItemOptionsApi itemOptions = new CertifiedCopyItemOptionsApi();
        itemOptions.setDeliveryMethod(DeliveryMethodApi.POSTAL);

        FilingHistoryDocumentApi filingHistoryDocumentApi = new FilingHistoryDocumentApi();
        filingHistoryDocumentApi.setFilingHistoryCost(ORDER_COST);
        filingHistoryDocumentApi.setFilingHistoryDate(FILING_HISTORY_DATE);
        filingHistoryDocumentApi.setFilingHistoryDescription(FILING_HISTORY_DESCRIPTION);
        filingHistoryDocumentApi.setFilingHistoryDescriptionValues(Collections.singletonMap("made_up_date", MADE_UP_DATE));
        filingHistoryDocumentApi.setFilingHistoryType(FILING_HISTORY_TYPE);
        itemOptions.setFilingHistoryDocuments(Collections.singletonList(filingHistoryDocumentApi));
        item.setItemOptions(itemOptions);

        order.setItems(Collections.singletonList(item));
        order.setTotalOrderCost(ORDER_COST);
        order.setPaymentReference(PAYMENT_REFERENCE);
        order.setOrderedAt(LocalDateTime.of(2021, 7, 27, 15,20,10));

        return order;
    }
}
