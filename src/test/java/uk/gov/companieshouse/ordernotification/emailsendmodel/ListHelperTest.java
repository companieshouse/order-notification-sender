package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class ListHelperTest {

    private ListHelper helper;

    @Test
    void listHelperAdd() {
        helper = new ListHelper(new ArrayList<>());
        helper.add(null, "testNull");
        helper.add(false, "testFalse");
        helper.add(true, "testTrue");
        assertEquals(helper.toList().get(0), "testTrue");
        assertTrue(helper.size() == 1);
    }
}
