package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Collections;
import java.util.List;

public class ListHelper {
    private List<String> list;

    public ListHelper(List<String> list) {
        this.list = list;
    }

    void add(boolean condition, String text) {
        if (condition) {
            list.add(text);
        }
    }

    List<String> getList() {
        return list;
    }

    int size() {
        return list.size();
    }

    List toList() {
        return Collections.unmodifiableList(list);
    }
}
