package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class CertificateDetailsModelBuilder {
    private final List<String> list;
    private Boolean includeBasicInformation;

    CertificateDetailsModelBuilder() {
        this.list = new ArrayList<>();
    }

    CertificateDetailsModelBuilder includeBasicInformation(Boolean includeBasicInformation) {
        this.includeBasicInformation = includeBasicInformation;
        return this;
    }

    CertificateDetailsModelBuilder includeText(Boolean condition, String text) {
        if (condition != null && condition) {
            list.add(text);
        }
        return this;
    }

    CertificateDetailsModel build() {
        return list.isEmpty() ?
                new CertificateDetailsModel(false, Collections.singletonList(MapUtil.mapBoolean(includeBasicInformation))) :
                new CertificateDetailsModel(true, Collections.unmodifiableList(list));
    }
}
