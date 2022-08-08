package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;
import uk.gov.companieshouse.api.model.order.item.DeliveryTimescaleApi;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CertificateEmailDataMapperTest {

    private static final String CERTIFICATE_ID = "CRT-123456-123456";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String FEE = "15";

    private static final String MAPPED_INCORPORATION_CERTIFICATE_TYPE = "Incorporation with all company name changes";
    private static final String MAPPED_DISSOLUTION_CERTIFICATE_TYPE = "Dissolution with all company name changes";
    private static final String MAPPED_STANDARD_DELIVERY_TEXT = "Standard delivery (aim to dispatch within 10 working days)";
    private static final String MAPPED_EXPRESS_DELIVERY_TEXT = "Express (Orders received before 11am will be dispatched the same day. Orders received after 11am will be dispatched the next working day)";
    private static final String MAPPED_FEE = "£15";

    private static final ItemBuilder standardCertificate = itemBuilder()
            .withId(CERTIFICATE_ID)
            .withCompanyNumber(COMPANY_NUMBER)
            .withTotalItemCost(FEE);

    private static final Certificate.CertificateBuilder mappedCertificate = Certificate.builder()
            .withId(CERTIFICATE_ID)
            .withCompanyNumber(COMPANY_NUMBER)
            .withFee(MAPPED_FEE);

    private static final ItemBuilder incorporation = standardCertificate.clone()
            .withDeliveryTimescale(DeliveryTimescaleApi.STANDARD)
            .withCertificateType(CertificateTypeApi.INCORPORATION_WITH_ALL_NAME_CHANGES);

    private static final Certificate.CertificateBuilder mappedIncorporation = mappedCertificate
            .clone()
            .withDeliveryMethod(MAPPED_STANDARD_DELIVERY_TEXT)
            .withCertificateType(MAPPED_INCORPORATION_CERTIFICATE_TYPE);

    private static final ItemBuilder dissolution = standardCertificate.clone()
            .withDeliveryTimescale(DeliveryTimescaleApi.SAME_DAY)
            .withCertificateType(CertificateTypeApi.DISSOLUTION);

    private static final Certificate.CertificateBuilder mappedDissolution = mappedCertificate
            .clone()
            .withDeliveryMethod(MAPPED_EXPRESS_DELIVERY_TEXT)
            .withCertificateType(MAPPED_DISSOLUTION_CERTIFICATE_TYPE);

    @InjectMocks
    private CertificateEmailDataMapper mapper;

    @Mock
    private CertificateTypeMapper typeMapper;

    @Mock
    private DeliveryMethodMapper deliveryMapper;

    @ParameterizedTest(name = "{0}")
    @MethodSource("getArguments")
    @DisplayName("Map certificate item to certificate model")
    void testMapCertificate(String name, ExpectationsBuilder expectationsBuilder) {
        // given
        BaseItemApi input = expectationsBuilder.buildItem();
        CertificateItemOptionsApi itemOptions = (CertificateItemOptionsApi) input.getItemOptions();
        Certificate expected = expectationsBuilder.buildCertificate();
        when(typeMapper.mapCertificateType(any())).thenReturn(expected.getCertificateType());
        when(deliveryMapper.mapDeliveryMethod(any(), any())).thenReturn(expected.getDeliveryMethod());

        // when
        Certificate actual = mapper.map(input);

        // then
        assertEquals(expected, actual);
        verify(typeMapper).mapCertificateType(itemOptions.getCertificateType());
        verify(deliveryMapper).mapDeliveryMethod(itemOptions.getDeliveryMethod(), itemOptions.getDeliveryTimescale());
    }

    static Stream<Arguments> getArguments() {
        return Stream.of(
                Arguments.of("Map certificate of incorporation with standard delivery", expectationsBuilder(incorporation, mappedIncorporation)),
                Arguments.of("Map certificate of dissolution with express delivery", expectationsBuilder(dissolution, mappedDissolution))
        );
    }

    static ExpectationsBuilder expectationsBuilder(ItemBuilder itemBuilder, Certificate.CertificateBuilder certificateBuilder) {
        return new ExpectationsBuilder(itemBuilder, certificateBuilder);
    }

    static ItemBuilder itemBuilder() {
        return new ItemBuilder();
    }

    static class ExpectationsBuilder {
        private final ItemBuilder itemBuilder;
        private final Certificate.CertificateBuilder certificateBuilder;

        public ExpectationsBuilder(ItemBuilder itemBuilder, Certificate.CertificateBuilder certificateBuilder) {
            this.itemBuilder = itemBuilder;
            this.certificateBuilder = certificateBuilder;
        }

        public Certificate buildCertificate() {
            return certificateBuilder.build();
        }

        public BaseItemApi buildItem() {
            return itemBuilder.build();
        }
    }

    static class ItemBuilder implements Cloneable {

        private String id;
        private CertificateTypeApi certificateType;
        private String companyNumber;
        private DeliveryTimescaleApi deliveryTimescale;
        private String totalItemCost;

        @Override
        protected ItemBuilder clone() {
            try {
                return (ItemBuilder) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }

        public ItemBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ItemBuilder withCertificateType(CertificateTypeApi certificateType) {
            this.certificateType = certificateType;
            return this;
        }

        public ItemBuilder withCompanyNumber(String companyNumber) {
            this.companyNumber = companyNumber;
            return this;
        }

        public ItemBuilder withDeliveryTimescale(DeliveryTimescaleApi deliveryTimescale) {
            this.deliveryTimescale = deliveryTimescale;
            return this;
        }

        public ItemBuilder withTotalItemCost(String totalItemCost) {
            this.totalItemCost = totalItemCost;
            return this;
        }

        BaseItemApi build() {
            BaseItemApi result = new BaseItemApi();
            CertificateItemOptionsApi itemOptionsApi = new CertificateItemOptionsApi();
            result.setId(id);
            result.setCompanyNumber(companyNumber);
            result.setTotalItemCost(totalItemCost);
            result.setItemOptions(itemOptionsApi);
            itemOptionsApi.setCertificateType(certificateType);
            itemOptionsApi.setDeliveryTimescale(deliveryTimescale);
            return result;
        }
    }
}
