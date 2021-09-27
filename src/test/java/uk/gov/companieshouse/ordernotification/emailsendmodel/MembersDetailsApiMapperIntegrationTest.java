package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.model.order.item.BaseMemberDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeDobTypeApi;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {MembersDetailsApiMapper.class})
@EnableConfigurationProperties
public class MembersDetailsApiMapperIntegrationTest {

    @Autowired
    private MembersDetailsApiMapper membersDetailsApiMapper;

    @Test
    void testCorrectlyMapsAllOptionsNull() {
        // Given
        BaseMemberDetailsApi sourceMemberDetails = new TestMemberDetails(null, null, null, null, null);

        // When
        CertificateDetailsModel result = membersDetailsApiMapper.map(sourceMemberDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(false, Collections.emptyList());

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsAllBooleanOptionsFalse() {
        // Given
        BaseMemberDetailsApi sourceMemberDetails = new TestMemberDetails(false, false, false, false, null);

        // When
        CertificateDetailsModel result = membersDetailsApiMapper.map(sourceMemberDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(false, Collections.emptyList());

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsAddressAllOptionsSelected() {
        // Given
        BaseMemberDetailsApi sourceMemberDetails = new TestMemberDetails(true, true, true, true, IncludeDobTypeApi.PARTIAL);

        // When
        CertificateDetailsModel result = membersDetailsApiMapper.map(sourceMemberDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>() {
            {
                add("Correspondence address");
                add("Appointment date");
                add("Country of residence");
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsAddressSelected() {
        // Given
        BaseMemberDetailsApi sourceMemberDetails = new TestMemberDetails(true, false,false, false, IncludeDobTypeApi.PARTIAL);

        // When
        CertificateDetailsModel result = membersDetailsApiMapper.map(sourceMemberDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>() {
            {
                add("Correspondence address");
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsAppintmentDateSelected() {
        // Given
        BaseMemberDetailsApi sourceMemberDetails = new TestMemberDetails(false, true,false, false, IncludeDobTypeApi.PARTIAL);

        // When
        CertificateDetailsModel result = membersDetailsApiMapper.map(sourceMemberDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>() {
            {
                add("Appointment date");
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsCountryOfResidenceSelected() {
        // Given
        BaseMemberDetailsApi sourceMemberDetails = new TestMemberDetails(false, false, true, true, IncludeDobTypeApi.PARTIAL);

        // When
        CertificateDetailsModel result = membersDetailsApiMapper.map(sourceMemberDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>() {
            {
                add("Country of residence");
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsincludeDobTypeSelected() {
        // Given
        BaseMemberDetailsApi sourceMemberDetails = new TestMemberDetails(false, false, false, false, IncludeDobTypeApi.PARTIAL);

        // When
        CertificateDetailsModel result = membersDetailsApiMapper.map(sourceMemberDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>() {
            {
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }
}
