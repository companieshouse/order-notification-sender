package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryItemOptionsApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.config.EmailDataConfiguration;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MissingImageDeliveryEmailDataMapperTest {

    @Mock
    private FilingHistoryDescriptionProviderService providerService;

    @Mock
    private EmailConfiguration config;

    @Mock
    private EmailDataConfiguration emailDataConfig;

    @InjectMocks
    private MissingImageDeliveryEmailDataMapper mapper;

    @Test
    void testMapMissingImageDelivery() {
        // given
        when(providerService.mapFilingHistoryDescription(eq(TestConstants.FILING_HISTORY_DESCRIPTION), any()))
                .thenReturn(TestConstants.MAPPED_FILING_HISTORY_DESCRIPTION);
        when(config.getMissingImage()).thenReturn(emailDataConfig);
        when(emailDataConfig.getFilingHistoryDateFormat()).thenReturn(TestConstants.FILING_HISTORY_EMAIL_DATE_FORMAT);

        // when
        MissingImageDelivery actual = mapper.map(getItem());

        // then
        MissingImageDelivery expected = getMappedMissingImageDelivery();
        assertEquals(expected, actual);
        verify(providerService).mapFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION, getFilingHistoryDescriptionValues());
    }

    private BaseItemApi getItem() {
        BaseItemApi baseItem = new BaseItemApi();
        baseItem.setId(TestConstants.MISSING_IMAGE_DELIVERY_ID);
        baseItem.setCompanyNumber(TestConstants.COMPANY_NUMBER);
        baseItem.setTotalItemCost(TestConstants.ORDER_COST);

        MissingImageDeliveryItemOptionsApi itemOptions = new MissingImageDeliveryItemOptionsApi();
        itemOptions.setFilingHistoryDate(TestConstants.FILING_HISTORY_DATE);
        itemOptions.setFilingHistoryType(TestConstants.FILING_HISTORY_TYPE);
        itemOptions.setFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION);
        itemOptions.setFilingHistoryDescriptionValues(getFilingHistoryDescriptionValues());

        baseItem.setItemOptions(itemOptions);
        return baseItem;
    }

    private MissingImageDelivery getMappedMissingImageDelivery() {
        return MissingImageDelivery.builder()
            .withId(TestConstants.MISSING_IMAGE_DELIVERY_ID)
            .withCompanyNumber(TestConstants.COMPANY_NUMBER)
            .withFee(TestConstants.ORDER_VIEW)
            .withDateFiled(TestConstants.MAPPED_FILING_HISTORY_DATE)
            .withType(TestConstants.FILING_HISTORY_TYPE)
            .withDescription(TestConstants.MAPPED_FILING_HISTORY_DESCRIPTION)
            .build();
    }

    private Map<String, Object> getFilingHistoryDescriptionValues() {
        Map<String, Object> filingHistoryDescriptionValues = new HashMap<>();
        filingHistoryDescriptionValues.put("made_up_date", TestConstants.MADE_UP_DATE);
        filingHistoryDescriptionValues.put("key", "value");
        return filingHistoryDescriptionValues;
    }
}
