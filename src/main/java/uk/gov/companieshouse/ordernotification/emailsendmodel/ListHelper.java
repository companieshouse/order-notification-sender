package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Collections;
import java.util.List;

class ListHelper {
    private final List<String> list;
    private final Boolean includeBasicInformation;

    ListHelper(List<String> list, Boolean includeBasicInformation) {
        this.list = list;
        this.includeBasicInformation = includeBasicInformation;
    }

    void add(Boolean condition, String text) {
        if (condition != null && condition) {
            list.add(text);
        }
    }

    CertificateDetailsModel certificateDetailsModel() {
        return list.isEmpty() ?
                new CertificateDetailsModel(false, Collections.singletonList(MapUtil.mapBoolean(includeBasicInformation))) :
                new CertificateDetailsModel(true, Collections.unmodifiableList(list));
    }
}
