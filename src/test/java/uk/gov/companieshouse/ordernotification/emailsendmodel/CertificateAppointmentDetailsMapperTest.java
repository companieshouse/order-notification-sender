package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CertificateAppointmentDetailsMapperTest {

    private CertificateAppointmentDetailsMapper mapper;

    @Mock
    private AppointmentDetailsDictionary dictionary;

    @BeforeEach
    void setup() {
        mapper = new CertificateAppointmentDetailsMapper(dictionary);
    }

    @Test
    void testMapAbsentAppointmentDetails() {
        //given
        when(dictionary.evaluateAppointment(any())).thenReturn(Collections.emptyList());
        when(dictionary.getText()).thenReturn(Collections.emptyList());

        //when
        CertificateAppointmentDetailsModel actual = mapper.mapAppointmentDetails(new DirectorOrSecretaryDetailsApi());

        //then
        assertEquals(expected(false, TestConstants.READABLE_FALSE), actual);
    }

    @Test
    void testMapBasicAppointmentDetails() {
        //given
        DirectorOrSecretaryDetailsApi appointment = new DirectorOrSecretaryDetailsApi();
        when(dictionary.evaluateAppointment(any())).thenReturn(Collections.emptyList());
        when(dictionary.getText()).thenReturn(Collections.emptyList());
        appointment.setIncludeBasicInformation(true);

        //when
        CertificateAppointmentDetailsModel actual = mapper.mapAppointmentDetails(appointment);

        //then
        assertEquals(expected(false, TestConstants.READABLE_TRUE), actual);
    }

    @Test
    void testMapSpecificDetails() {
        //given
        DirectorOrSecretaryDetailsApi appointment = new DirectorOrSecretaryDetailsApi();
        when(dictionary.evaluateAppointment(any())).thenReturn(Collections.singletonList(true));
        when(dictionary.getText()).thenReturn(Collections.singletonList("Selection"));

        //when
        CertificateAppointmentDetailsModel actual = mapper.mapAppointmentDetails(appointment);

        //then
        assertEquals(expected(true, "Selection"), actual);
    }

    private CertificateAppointmentDetailsModel expected(boolean specificDetails, String... choices) {
        return new CertificateAppointmentDetailsModel(specificDetails, Arrays.asList(choices));
    }
}
