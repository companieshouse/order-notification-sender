package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.ActionedByApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.orders.service.OrdersApiDetails;

@ExtendWith(MockitoExtension.class)
class MissingImageOrderDetailsMapperTest {

    @Mock
    private FilingHistoryDescriptionProviderService providerService;

    @Mock
    private EmailConfiguration config;

    @Mock
    private OrdersApiDetailsCommonFieldsMapper commonFieldsMapper;

    @InjectMocks
    private MissingImageOrderDetailsMapper mapper;

    @Mock
    private EmailDataConfiguration emailDataConfig;

    @Mock
    private OrdersApiDetails ordersApiDetails;

    @Test
    void testMapMissingImageItemToMissingItemNotificationModel() {
        // given
        OrdersApi order = getOrder();
        when(ordersApiDetails.getBaseItemOptions()).thenReturn(order.getItems().get(0).getItemOptions());
        when(providerService.mapFilingHistoryDescription(eq(TestConstants.FILING_HISTORY_DESCRIPTION), any()))
                .thenReturn(TestConstants.MAPPED_FILING_HISTORY_DESCRIPTION);
        when(config.getMissingImage()).thenReturn(emailDataConfig);
        when(emailDataConfig.getFilingHistoryDateFormat()).thenReturn(TestConstants.EMAIL_DATE_FORMAT);

        // when
        MissingImageOrderNotificationModel result = mapper.map(ordersApiDetails);

        // then
        assertEquals(getExpectedModel(), result);
        verify(providerService).mapFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION,
                Collections.singletonMap("key", "value"));
    }

    private MissingImageOrderNotificationModel getExpectedModel() {

        FilingHistoryDetailsModel details = new FilingHistoryDetailsModel();
        details.setFilingHistoryDate(TestConstants.FILING_HISTORY_DATE_VIEW);
        details.setFilingHistoryDescription(TestConstants.MAPPED_FILING_HISTORY_DESCRIPTION);
        details.setFilingHistoryType(TestConstants.FILING_HISTORY_TYPE);

        MissingImageOrderNotificationModel expected = new MissingImageOrderNotificationModel();
        expected.setFilingHistoryDetails(details);

        return expected;
    }


    private OrdersApi getOrder() {
        OrdersApi ordersApi = new OrdersApi();
        ordersApi.setReference(TestConstants.ORDER_REFERENCE_NUMBER);
        ordersApi.setTotalOrderCost(TestConstants.ORDER_COST);
        ordersApi.setPaymentReference(TestConstants.PAYMENT_REFERENCE);
        ordersApi.setOrderedAt(LocalDateTime.of(2021, 7, 27, 15,20,10));
        ActionedByApi orderedBy = new ActionedByApi();
        orderedBy.setEmail(TestConstants.EMAIL_RECIPIENT);
        ordersApi.setOrderedBy(orderedBy);

        MissingImageDeliveryApi missingImageItem = new MissingImageDeliveryApi();
        missingImageItem.setCompanyName(TestConstants.COMPANY_NAME);
        missingImageItem.setCompanyNumber(TestConstants.COMPANY_NUMBER);

        MissingImageDeliveryItemOptionsApi itemOptions = new MissingImageDeliveryItemOptionsApi();
        itemOptions.setFilingHistoryDate(TestConstants.FILING_HISTORY_DATE);
        itemOptions.setFilingHistoryType(TestConstants.FILING_HISTORY_TYPE);
        itemOptions.setFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION);
        itemOptions.setFilingHistoryDescriptionValues(Collections.singletonMap("key", "value"));
        missingImageItem.setItemOptions(itemOptions);
        ordersApi.setItems(Collections.singletonList(missingImageItem));
        return ordersApi;
    }
}
