package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ListHelperTest {

    private ListHelper helper;

    @Test
    void correctlyBuildModelWhenListIsEmptyAndBasicInformationNull() {
        helper = new ListHelper(new ArrayList<>(), null);

        CertificateDetailsModel expected = new CertificateDetailsModel(false, new ArrayList<String>() {
            {
                add("No");
            }
        });
        CertificateDetailsModel result = helper.certificateDetailsModel();
        assertEquals(result, expected);
    }

    @Test
    void correctlyBuildModelWhenListIsEmptyAndBasicInformationFalse() {
        helper = new ListHelper(new ArrayList<>(), null);

        CertificateDetailsModel expected = new CertificateDetailsModel(false, new ArrayList<String>() {
            {
                add("No");
            }
        });
        assertEquals(helper.certificateDetailsModel(), expected);
    }

    @Test
    void correctlyBuildModelWhenListIsEmptyAndBasicInformationTrue() {
        helper = new ListHelper(new ArrayList<>(), true);

        CertificateDetailsModel expected = new CertificateDetailsModel(false, new ArrayList<String>() {
            {
                add("Yes");
            }
        });
        assertEquals(helper.certificateDetailsModel(), expected);
    }

    @Test
    void correctlyBuildModelWhenListHasEntriesBasicInformationFalse() {
        helper = new ListHelper(new ArrayList<>(), false);
        helper.add(null, "testNull");
        helper.add(false, "testFalse");
        helper.add(true, "testTrue");

        CertificateDetailsModel expected = new CertificateDetailsModel(true, new ArrayList<String>() {
            {
                add("testTrue");
            }
        });
        assertEquals(helper.certificateDetailsModel(), expected);
    }

    @Test
    void correctlyBuildModelWhenListHasEntriesBasicInformationTrue() {
        helper = new ListHelper(new ArrayList<>(), true);
        helper.add(null, "testNull");
        helper.add(false, "testFalse");
        helper.add(true, "testTrue");

        CertificateDetailsModel expected = new CertificateDetailsModel(true, new ArrayList<String>() {
            {
                add("testTrue");
            }
        });
        assertEquals(helper.certificateDetailsModel(), expected);
    }
}
