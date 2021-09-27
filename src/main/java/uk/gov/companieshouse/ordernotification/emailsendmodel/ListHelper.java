package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Collections;
import java.util.List;

class ListHelper {
    private List<String> list;

    ListHelper(List<String> list) {
        this.list = list;
    }

    void add(Boolean condition, String text) {
        if (condition != null && condition) {
            list.add(text);
        }
    }

    int size() {
        return list.size();
    }

    List toList() {
        return Collections.unmodifiableList(list);
    }
}
