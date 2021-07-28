package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryItemOptionsApi;
import uk.gov.companieshouse.ordernotification.emailsender.EmailSend;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissingImageOrderNotificationMapperTest {

    private MissingImageOrderNotificationMapper mapper;

    @Mock
    private DateGenerator dateGenerator;

    @BeforeEach
    void setup() {
        mapper = new MissingImageOrderNotificationMapper(dateGenerator, TestConstants.EMAIL_DATE_FORMAT,
                TestConstants.SENDER_EMAIL_ADDRESS, TestConstants.PAYMENT_DATE_FORMAT,  TestConstants.MESSAGE_ID,
                TestConstants.APPLICATION_ID, TestConstants.MESSAGE_TYPE);
    }

    @Test
    void testMapMissingImageItemToMissingItemNotificationModel() throws JsonProcessingException {
        // given
        OrdersApi order = getOrder();
        when(dateGenerator.generate()).thenReturn(LocalDateTime.of(2021, 7, 27, 15, 20, 10));

        // when
        EmailSend result = mapper.map(order);

        // then
        assertEquals(getExpectedEmailSendModel(), result);
    }

    private EmailSend getExpectedEmailSendModel() throws JsonProcessingException {
        EmailSend expected = new EmailSend();
        expected.setAppId(TestConstants.APPLICATION_ID);
        OrderModel model = getExpectedModel();
        model.setOrderReferenceNumber(TestConstants.ORDER_REFERENCE_NUMBER);
        model.setPaymentReference(TestConstants.PAYMENT_REFERENCE);
        model.setPaymentTime(TestConstants.PAYMENT_TIME);
        model.setTotalOrderCost(TestConstants.ORDER_COST);
        expected.setData(new ObjectMapper().writeValueAsString(model));
        expected.setCreatedAt("27 July 2021");
        expected.setEmailAddress(TestConstants.SENDER_EMAIL_ADDRESS);
        expected.setMessageId(TestConstants.MESSAGE_ID);
        expected.setMessageType(TestConstants.MESSAGE_TYPE);
        return expected;
    }

    private MissingImageOrderNotificationModel getExpectedModel() {

        FilingHistoryDetailsModel details = new FilingHistoryDetailsModel();
        details.setFilingHistoryDate(TestConstants.FILING_HISTORY_DATE);
        details.setFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION);
        details.setFilingHistoryType(TestConstants.FILING_HISTORY_TYPE);

        MissingImageOrderNotificationModel expected = new MissingImageOrderNotificationModel();
        expected.setCompanyName(TestConstants.COMPANY_NAME);
        expected.setCompanyNumber(TestConstants.COMPANY_NUMBER);
        expected.setFilingHistoryDetails(details);

        return expected;
    }


    private OrdersApi getOrder() {
        OrdersApi ordersApi = new OrdersApi();
        ordersApi.setReference(TestConstants.ORDER_REFERENCE_NUMBER);
        ordersApi.setTotalOrderCost(TestConstants.ORDER_COST);
        ordersApi.setPaymentReference(TestConstants.PAYMENT_REFERENCE);
        ordersApi.setOrderedAt(LocalDateTime.of(2021, 7, 27, 15,20,10));

        MissingImageDeliveryApi missingImageItem = new MissingImageDeliveryApi();
        missingImageItem.setCompanyName(TestConstants.COMPANY_NAME);
        missingImageItem.setCompanyNumber(TestConstants.COMPANY_NUMBER);

        MissingImageDeliveryItemOptionsApi itemOptions = new MissingImageDeliveryItemOptionsApi();
        itemOptions.setFilingHistoryDate(TestConstants.FILING_HISTORY_DATE);
        itemOptions.setFilingHistoryType(TestConstants.FILING_HISTORY_TYPE);
        itemOptions.setFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION);
        missingImageItem.setItemOptions(itemOptions);
        ordersApi.setItems(Collections.singletonList(missingImageItem));
        return ordersApi;
    }
}
