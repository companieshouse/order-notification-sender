package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.DesignatedMemberDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;
import uk.gov.companieshouse.api.model.order.item.IncludeDobTypeApi;
import uk.gov.companieshouse.api.model.order.item.MemberDetailsApi;
import uk.gov.companieshouse.api.model.order.item.RegisteredOfficeAddressDetailsApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LLPCertificateOptionsMapperTest {

    @Mock
    private AddressRecordTypeMapper addressRecordTypeMapper;
    @Mock
    private MembersDetailsApiMapper membersDetailsApiMapper;
    @Mock
    private CompanyStatusMapper companyStatusMapper;

    @InjectMocks
    private LLPCertificateOptionsMapper llpCertificateOptionsMapper;

    @Test
    void doMapCustomDataMapsCorrectly() {
        // given
        DesignatedMemberDetailsApi designatedMembersDetails = new DesignatedMemberDetailsApi();
        designatedMembersDetails.setIncludeAddress(true);
        designatedMembersDetails.setIncludeAppointmentDate(true);
        designatedMembersDetails.setIncludeBasicInformation(true);
        designatedMembersDetails.setIncludeCountryOfResidence(true);
        designatedMembersDetails.setIncludeDobType(IncludeDobTypeApi.FULL);
        
        MemberDetailsApi membersDetails = new MemberDetailsApi();
        membersDetails.setIncludeAddress(true);
        membersDetails.setIncludeAppointmentDate(true);
        membersDetails.setIncludeBasicInformation(true);
        membersDetails.setIncludeCountryOfResidence(true);
        membersDetails.setIncludeDobType(IncludeDobTypeApi.FULL);

        CertificateItemOptionsApi itemOptions = new CertificateItemOptionsApi();

        RegisteredOfficeAddressDetailsApi registeredOfficeAddressDetails = new RegisteredOfficeAddressDetailsApi();
        registeredOfficeAddressDetails.setIncludeAddressRecordsType(IncludeAddressRecordsTypeApi.CURRENT_PREVIOUS_AND_PRIOR);

        itemOptions.setRegisteredOfficeAddressDetails(registeredOfficeAddressDetails);
        itemOptions.setDesignatedMemberDetails(designatedMembersDetails);
        itemOptions.setMemberDetails(membersDetails);

        CertificateOrderNotificationModel result = new CertificateOrderNotificationModel();

        when(addressRecordTypeMapper.mapAddressRecordType(any())).thenReturn(TestConstants.ADDRESS_TYPE);
        when(membersDetailsApiMapper.map(any())).thenReturn(getCertificateDetailsModel());

        // when
        llpCertificateOptionsMapper.doMapCustomData(itemOptions, result);

        // then
        assertEquals(getCertificateOrderNotificationModel(), result);
        verify(companyStatusMapper).map(eq(itemOptions), any());
    }

    private CertificateOrderNotificationModel getCertificateOrderNotificationModel() {
        CertificateOrderNotificationModel model = new CertificateOrderNotificationModel();
        model.setDesignatedMembersDetails(getCertificateDetailsModel());
        model.setMembersDetails(getCertificateDetailsModel());
        model.setRegisteredOfficeAddressDetails(TestConstants.ADDRESS_TYPE);
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
