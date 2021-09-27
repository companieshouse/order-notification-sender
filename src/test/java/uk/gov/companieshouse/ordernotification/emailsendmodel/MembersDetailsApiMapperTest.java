package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.model.order.item.BaseMemberDetailsApi;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {MembersDetailsApiMapper.class})
@EnableConfigurationProperties
public class MembersDetailsApiMapperTest {

    @Autowired
    private MembersDetailsApiMapper membersDetailsApiMapper;

    @Test
    void testCorrectlyMapsAllOptionsNullSelected() {
        // Given
        BaseMemberDetailsApi sourceMemberDetails = new TestMemberDetails(null, null, null, null, null);

        // When
        CertificateDetailsModel result = membersDetailsApiMapper.map(sourceMemberDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(false, Collections.emptyList());

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsAllOptionsFalseSelected() {

    }

    @Test
    void testCorrectlyMapsAddressOptionsSelected() {

    }

    @Test
    void testCorrectlyMapsAppointmentDateSelected() {

    }

    @Test
    void testCorrectlyMapsCountryOfResidenceSelected() {

    }

    @Test
    void testCorrectlyMapsincludeDobTypeSelected() {

    }

    @Test
    void testCorrectlyMapsAllOptionsTrueSelected() {

    }

}
