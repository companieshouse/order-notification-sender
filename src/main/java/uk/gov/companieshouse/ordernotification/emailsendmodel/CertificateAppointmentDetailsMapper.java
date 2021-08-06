package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.DirectorOrSecretaryDetailsApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class CertificateAppointmentDetailsMapper {

    private AppointmentDetailsDictionary dictionary;

    @Autowired
    public CertificateAppointmentDetailsMapper(AppointmentDetailsDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public CertificateAppointmentDetailsModel mapAppointmentDetails(DirectorOrSecretaryDetailsApi appointmentDetails) {
        List<Boolean> requestedData = dictionary.evaluateAppointment(appointmentDetails);
        List<String> text = dictionary.getText();
        List<String> result = IntStream.range(0, text.size())
                .filter(a -> evaluateBooleanWrapper(requestedData.get(a)))
                .mapToObj(text::get)
                .collect(Collectors.toCollection(ArrayList::new));
        if(!result.isEmpty()){
            return new CertificateAppointmentDetailsModel(true, result);
        } else if(evaluateBooleanWrapper(appointmentDetails.getIncludeBasicInformation())) {
            return new CertificateAppointmentDetailsModel(false, Collections.singletonList("Yes"));
        } else {
            return new CertificateAppointmentDetailsModel(false, Collections.singletonList("No"));
        }
    }

    private boolean evaluateBooleanWrapper(Boolean val) {
        return val != null && val;
    }
}
