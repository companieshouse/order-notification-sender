package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.api.model.order.item.IncludeDobTypeApi;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppointmentDetailsDictionaryTest {

    private AppointmentDetailsDictionary dictionary;

    @BeforeEach
    void setup() {
        dictionary = new AppointmentDetailsDictionary();
    }

    @Test
    void testEvaluateAppointment() {
        //given
        DirectorOrSecretaryDetailsApi appointment = new DirectorOrSecretaryDetailsApi();
        appointment.setIncludeAddress(true);
        appointment.setIncludeAppointmentDate(true);
        appointment.setIncludeCountryOfResidence(true);
        appointment.setIncludeDobType(IncludeDobTypeApi.PARTIAL);
        appointment.setIncludeOccupation(true);
        appointment.setIncludeNationality(true);

        //when
        List<Boolean> actual = dictionary.evaluateAppointment(appointment);

        //then
        assertEquals(Arrays.asList(true, true, true, true, true, true), actual);
    }

    @Test
    void testGetText() {
        //given
        dictionary.setAddress("A");
        dictionary.setAppointmentDate("B");
        dictionary.setCountryOfResidence("C");
        dictionary.setNationality("D");
        dictionary.setOccupation("E");
        dictionary.setDob("F");

        //when
        List<String> actual = dictionary.getText();

        //then
        assertEquals(Arrays.asList("A", "B", "C", "D", "E", "F"), actual);
    }
}
