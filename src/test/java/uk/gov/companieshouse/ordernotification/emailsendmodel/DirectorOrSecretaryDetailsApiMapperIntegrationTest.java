package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeDobTypeApi;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {DirectorOrSecretaryDetailsApiMapper.class})
@EnableConfigurationProperties
class DirectorOrSecretaryDetailsApiMapperIntegrationTest {
    @Autowired
    private DirectorOrSecretaryDetailsApiMapper directorOrSecretaryDetailsApiMapper;

    @Test
    void testCorrectlyMapsAllOptionsNull() {
        // Given
        DirectorOrSecretaryDetailsApi sourceDetails = new DirectorOrSecretaryDetailsApi();

        // When
        CertificateDetailsModel result = directorOrSecretaryDetailsApiMapper.map(sourceDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(false, Collections.singletonList("No"));

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsAllBooleanOptionsFalse() {
        // Given
        DirectorOrSecretaryDetailsApi sourceDetails = new DirectorOrSecretaryDetailsApi();
        sourceDetails.setIncludeAddress(false);
        sourceDetails.setIncludeAppointmentDate(false);
        sourceDetails.setIncludeBasicInformation(false);
        sourceDetails.setIncludeDobType(null);
        sourceDetails.setIncludeNationality(false);
        sourceDetails.setIncludeCountryOfResidence(false);
        sourceDetails.setIncludeOccupation(false);

        // When
        CertificateDetailsModel result = directorOrSecretaryDetailsApiMapper.map(sourceDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(false, Collections.singletonList("No"));

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsOnlyBasicInformationIsTrue() {
        // Given
        DirectorOrSecretaryDetailsApi sourceDetails = new DirectorOrSecretaryDetailsApi();
        sourceDetails.setIncludeAddress(false);
        sourceDetails.setIncludeAppointmentDate(false);
        sourceDetails.setIncludeBasicInformation(true);
        sourceDetails.setIncludeDobType(null);
        sourceDetails.setIncludeNationality(false);
        sourceDetails.setIncludeCountryOfResidence(false);
        sourceDetails.setIncludeOccupation(false);

        // When
        CertificateDetailsModel result = directorOrSecretaryDetailsApiMapper.map(sourceDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(false, Collections.singletonList("Yes"));

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsAllOptionsTrue() {
        // Given
        DirectorOrSecretaryDetailsApi sourceDetails = new DirectorOrSecretaryDetailsApi();
        sourceDetails.setIncludeAddress(true);
        sourceDetails.setIncludeAppointmentDate(true);
        sourceDetails.setIncludeBasicInformation(true);
        sourceDetails.setIncludeDobType(IncludeDobTypeApi.PARTIAL);
        sourceDetails.setIncludeNationality(true);
        sourceDetails.setIncludeCountryOfResidence(true);
        sourceDetails.setIncludeOccupation(true);

        // When
        CertificateDetailsModel result = directorOrSecretaryDetailsApiMapper.map(sourceDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>(){
            {
                add("Correspondence address");
                add("Appointment date");
                add("Country of residence");
                add("Nationality");
                add("Occupation");
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }

    @Test
    void testCorrectlyMapsAddressOptions() {
        // Given
        DirectorOrSecretaryDetailsApi sourceDetails = new DirectorOrSecretaryDetailsApi();
        sourceDetails.setIncludeAddress(true);
        sourceDetails.setIncludeAppointmentDate(false);
        sourceDetails.setIncludeBasicInformation(false);
        sourceDetails.setIncludeDobType(IncludeDobTypeApi.PARTIAL);
        sourceDetails.setIncludeNationality(false);
        sourceDetails.setIncludeCountryOfResidence(false);
        sourceDetails.setIncludeOccupation(false);

        // When
        CertificateDetailsModel result = directorOrSecretaryDetailsApiMapper.map(sourceDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>(){
            {
                add("Correspondence address");
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }
    @Test
    void testCorrectlyMapsAppointmentDate() {
        // Given
        DirectorOrSecretaryDetailsApi sourceDetails = new DirectorOrSecretaryDetailsApi();
        sourceDetails.setIncludeAddress(false);
        sourceDetails.setIncludeAppointmentDate(true);
        sourceDetails.setIncludeBasicInformation(false);
        sourceDetails.setIncludeDobType(IncludeDobTypeApi.PARTIAL);
        sourceDetails.setIncludeNationality(false);
        sourceDetails.setIncludeCountryOfResidence(false);
        sourceDetails.setIncludeOccupation(false);

        // When
        CertificateDetailsModel result = directorOrSecretaryDetailsApiMapper.map(sourceDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>(){
            {
                add("Appointment date");
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }

        @Test
    void testCorrectlyMapsIncludeDobType() {
        // Given
        DirectorOrSecretaryDetailsApi sourceDetails = new DirectorOrSecretaryDetailsApi();
        sourceDetails.setIncludeAddress(false);
        sourceDetails.setIncludeAppointmentDate(false);
        sourceDetails.setIncludeBasicInformation(false);
        sourceDetails.setIncludeDobType(IncludeDobTypeApi.PARTIAL);
        sourceDetails.setIncludeNationality(false);
        sourceDetails.setIncludeCountryOfResidence(false);
        sourceDetails.setIncludeOccupation(false);

        // When
        CertificateDetailsModel result = directorOrSecretaryDetailsApiMapper.map(sourceDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>(){
            {
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }

        @Test
    void testCorrectlyMapsNationality() {
        // Given
        DirectorOrSecretaryDetailsApi sourceDetails = new DirectorOrSecretaryDetailsApi();
        sourceDetails.setIncludeAddress(false);
        sourceDetails.setIncludeAppointmentDate(false);
        sourceDetails.setIncludeBasicInformation(false);
        sourceDetails.setIncludeDobType(IncludeDobTypeApi.PARTIAL);
        sourceDetails.setIncludeNationality(true);
        sourceDetails.setIncludeCountryOfResidence(false);
        sourceDetails.setIncludeOccupation(false);

        // When
        CertificateDetailsModel result = directorOrSecretaryDetailsApiMapper.map(sourceDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>(){
            {
                add("Nationality");
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }

        @Test
    void testCorrectlyMapsCountryOfResidence() {
        // Given
        DirectorOrSecretaryDetailsApi sourceDetails = new DirectorOrSecretaryDetailsApi();
        sourceDetails.setIncludeAddress(false);
        sourceDetails.setIncludeAppointmentDate(false);
        sourceDetails.setIncludeBasicInformation(false);
        sourceDetails.setIncludeDobType(IncludeDobTypeApi.PARTIAL);
        sourceDetails.setIncludeNationality(false);
        sourceDetails.setIncludeCountryOfResidence(true);
        sourceDetails.setIncludeOccupation(false);

        // When
        CertificateDetailsModel result = directorOrSecretaryDetailsApiMapper.map(sourceDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>(){
            {
                add("Country of residence");
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }

        @Test
    void testCorrectlyMapsOccupation() {
        // Given
        DirectorOrSecretaryDetailsApi sourceDetails = new DirectorOrSecretaryDetailsApi();
        sourceDetails.setIncludeAddress(false);
        sourceDetails.setIncludeAppointmentDate(false);
        sourceDetails.setIncludeBasicInformation(false);
        sourceDetails.setIncludeDobType(IncludeDobTypeApi.PARTIAL);
        sourceDetails.setIncludeNationality(false);
        sourceDetails.setIncludeCountryOfResidence(false);
        sourceDetails.setIncludeOccupation(true);

        // When
        CertificateDetailsModel result = directorOrSecretaryDetailsApiMapper.map(sourceDetails);

        // Then
        CertificateDetailsModel expected = new CertificateDetailsModel(true, Collections.unmodifiableList(new ArrayList<String>(){
            {
                add("Occupation");
                add("Date of birth (month and year)");
            }
        }));

        assertEquals(result, expected);
    }
}
