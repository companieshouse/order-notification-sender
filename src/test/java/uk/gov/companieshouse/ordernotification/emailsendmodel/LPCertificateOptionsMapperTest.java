package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.GeneralPartnerDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;
import uk.gov.companieshouse.api.model.order.item.LimitedPartnerDetailsApi;
import uk.gov.companieshouse.api.model.order.item.PrincipalPlaceOfBusinessDetailsApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LPCertificateOptionsMapperTest {

    @Mock
    private AddressRecordTypeMapper addressRecordTypeMapper;

    @InjectMocks
    private LPCertificateOptionsMapper lpCertificateOptionsMapper;

    @Test
    void doMapCustomDataMapsCorrectly() {
        // given
        CertificateItemOptionsApi itemOptions = new CertificateItemOptionsApi();

        PrincipalPlaceOfBusinessDetailsApi principalPlaceOfBusinessDetailsApi = new PrincipalPlaceOfBusinessDetailsApi();
        principalPlaceOfBusinessDetailsApi.setIncludeAddressRecordsType(IncludeAddressRecordsTypeApi.CURRENT_PREVIOUS_AND_PRIOR);

        itemOptions.setPrincipalPlaceOfBusinessDetails(principalPlaceOfBusinessDetailsApi);
        itemOptions.setGeneralPartnerDetails(new GeneralPartnerDetailsApi(){
            {
                setIncludeBasicInformation(true);
            }
        });
        itemOptions.setLimitedPartnerDetails(new LimitedPartnerDetailsApi(){
            {
                setIncludeBasicInformation(true);
            }
        });
        itemOptions.setIncludeGeneralNatureOfBusinessInformation(true);

        CertificateOrderNotificationModel result = new CertificateOrderNotificationModel();

        when(addressRecordTypeMapper.mapAddressRecordType(any())).thenReturn(TestConstants.ADDRESS_TYPE);

        // when
        lpCertificateOptionsMapper.doMapCustomData(itemOptions, result);

        // then
        assertEquals(getCertificateOrderNotificationModel(), result);
    }

    private CertificateOrderNotificationModel getCertificateOrderNotificationModel() {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        model.setPrincipalPlaceOfBusinessDetails(TestConstants.ADDRESS_TYPE);
        model.setGeneralPartnerDetails("Yes");
        model.setLimitedPartnerDetails("Yes");
        model.setGeneralNatureOfBusinessInformation("Yes");
        return model;
    }
    private CertificateDetailsModel getCertificateDetailsModel() {
        return new CertificateDetailsModel(true, new ArrayList<String>() {
            {
                add("Correspondence address");
                add("Appointment date");
                add("Country of residence");
                add("Nationality");
                add("Occupation");
                add("Date of birth (month and year)");
            }
        });
    }

}
