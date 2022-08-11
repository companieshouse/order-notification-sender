package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;
import uk.gov.companieshouse.api.model.order.item.FilingHistoryDocumentApi;
import uk.gov.companieshouse.ordernotification.config.EmailConfiguration;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

@ExtendWith(MockitoExtension.class)
public class CertifiedCopyEmailDataMapperTest {
    private static final BaseItemBuilder baseCertifiedCopy = baseItemBuilder()
            .withId(TestConstants.CERTIFIED_COPY_ID)
            .withCompanyNumber(TestConstants.COMPANY_NUMBER)
            .withFilingHistoryDocument(getFilingHistoryDocumentApiList());

    private static final CertifiedCopy.CertifiedCopyBuilder mappedBaseCertifiedCopy =
            CertifiedCopy.builder()
                .withId(TestConstants.CERTIFIED_COPY_ID)
                .withCompanyNumber(TestConstants.COMPANY_NUMBER)
                .withDateFiled(TestConstants.MAPPED_FILING_HISTORY_DATE)
                .withType(TestConstants.FILING_HISTORY_TYPE)
                .withDescription(TestConstants.MAPPED_FILING_HISTORY_DESCRIPTION)
                .withFee(TestConstants.ORDER_VIEW);

    private static final BaseItemBuilder standardDelivery = baseCertifiedCopy.clone()
            .withDeliveryTimescale(DeliveryTimescaleApi.STANDARD);

    private static final CertifiedCopy.CertifiedCopyBuilder mappedStandardDelivery =
            mappedBaseCertifiedCopy.clone()
                .withDeliveryMethod(TestConstants.MAPPED_STANDARD_DELIVERY_TEXT);

    private static final BaseItemBuilder expressDelivery = baseCertifiedCopy.clone()
            .withDeliveryTimescale(DeliveryTimescaleApi.SAME_DAY);

    private static final CertifiedCopy.CertifiedCopyBuilder mappedExpressDelivery =
            mappedBaseCertifiedCopy.clone()
                .withDeliveryMethod(TestConstants.MAPPED_EXPRESS_DELIVERY_TEXT);

    @InjectMocks
    private CertifiedCopyEmailDataMapper mapper;

    @Mock
    private DeliveryMethodMapper deliveryMapper;

    @Mock
    private EmailConfiguration config;

    @Mock
    private FilingHistoryDescriptionProviderService providerService;

    @ParameterizedTest(name = "{0}")
    @MethodSource("getArguments")
    @DisplayName("Map certified copy item to certified copy model")
    void testMapCertifiedCopy(String name, ExpectationsBuilder expectationsBuilder) {
        // given
        BaseItemApi input = expectationsBuilder.buildBaseItem();
        CertifiedCopyItemOptionsApi itemOptions = (CertifiedCopyItemOptionsApi) input.getItemOptions();
        CertifiedCopy expected = expectationsBuilder.buildCertifiedCopy();

        when(deliveryMapper.mapDeliveryMethod(any(), any())).thenReturn(expected.getDeliveryMethod());
        when(config.getFilingHistoryDateFormat()).thenReturn(TestConstants.FILING_HISTORY_EMAIL_DATE_FORMAT);
        when(providerService.mapFilingHistoryDescription(eq(TestConstants.FILING_HISTORY_DESCRIPTION), any()))
                .thenReturn(TestConstants.MAPPED_FILING_HISTORY_DESCRIPTION);

        // when
        CertifiedCopy actual = mapper.map(input);

        // then
        assertEquals(expected, actual);
        verify(deliveryMapper).mapDeliveryMethod(itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale());
        verify(providerService).mapFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION, getFilingHistoryDescriptionValues());
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of("Map a certified copy with standard delivery option",
                        expectationsBuilder(standardDelivery, mappedStandardDelivery)),
                Arguments.of("Map a certified copy with express delivery option",
                        expectationsBuilder(expressDelivery, mappedExpressDelivery))
        );
    }

    static ExpectationsBuilder expectationsBuilder(BaseItemBuilder baseItemBuilder, CertifiedCopy.CertifiedCopyBuilder certifiedCopyBuilder) {
        return new ExpectationsBuilder(baseItemBuilder, certifiedCopyBuilder);
    }

    static BaseItemBuilder baseItemBuilder() {
        return new BaseItemBuilder();
    }

    static class ExpectationsBuilder {
        private final BaseItemBuilder baseItemBuilder;
        private final CertifiedCopy.CertifiedCopyBuilder certifiedCopyBuilder;

        public ExpectationsBuilder(BaseItemBuilder baseItemBuilder, CertifiedCopy.CertifiedCopyBuilder certifiedCopyBuilder) {
            this.baseItemBuilder = baseItemBuilder;
            this.certifiedCopyBuilder = certifiedCopyBuilder;
        }

        public CertifiedCopy buildCertifiedCopy() {
            return certifiedCopyBuilder.build();
        }

        public BaseItemApi buildBaseItem() {
            return baseItemBuilder.build();
        }
    }

    static class BaseItemBuilder implements Cloneable {
        private String id;
        private String companyNumber;
        private DeliveryTimescaleApi deliveryTimescale;
        private List<FilingHistoryDocumentApi> filingHistoryDocumentApis;

        @Override
        protected BaseItemBuilder clone() {
            try {
                return (BaseItemBuilder) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public BaseItemBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public BaseItemBuilder withCompanyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        public BaseItemBuilder withDeliveryTimescale(DeliveryTimescaleApi deliveryTimescale) {
            this.deliveryTimescale = deliveryTimescale;
            return this;
        }

        public BaseItemBuilder withFilingHistoryDocument(List<FilingHistoryDocumentApi> filingHistoryDocumentApis) {
            this.filingHistoryDocumentApis = filingHistoryDocumentApis;
            return this;
        }

        BaseItemApi build() {
            BaseItemApi result = new BaseItemApi();
            CertifiedCopyItemOptionsApi itemOptions = new CertifiedCopyItemOptionsApi();
            result.setId(id);
            result.setCompanyNumber(companyNumber);
            itemOptions.setDeliveryTimescale(deliveryTimescale);
            itemOptions.setFilingHistoryDocuments(filingHistoryDocumentApis);
            result.setItemOptions(itemOptions);

            return result;
        }
    }

    private static List<FilingHistoryDocumentApi> getFilingHistoryDocumentApiList() {
        Map<String, Object> filingHistoryDescriptionValues = new HashMap<>();
        filingHistoryDescriptionValues.put("made_up_date", TestConstants.MADE_UP_DATE);

        FilingHistoryDocumentApi filingHistoryDocument = new FilingHistoryDocumentApi();
        filingHistoryDocument.setFilingHistoryCost(TestConstants.ORDER_COST);
        filingHistoryDocument.setFilingHistoryDate(TestConstants.FILING_HISTORY_DATE);
        filingHistoryDocument.setFilingHistoryType(TestConstants.FILING_HISTORY_TYPE);
        filingHistoryDocument.setFilingHistoryDescription(TestConstants.FILING_HISTORY_DESCRIPTION);
        filingHistoryDocument.setFilingHistoryDescriptionValues(getFilingHistoryDescriptionValues());

        return Collections.singletonList(filingHistoryDocument);
    }

    private static Map<String, Object> getFilingHistoryDescriptionValues() {
        Map<String, Object> filingHistoryDescriptionValues = new HashMap<>();
        filingHistoryDescriptionValues.put("made_up_date", TestConstants.MADE_UP_DATE);
        filingHistoryDescriptionValues.put("key", "value");
        return filingHistoryDescriptionValues;
    }
}
