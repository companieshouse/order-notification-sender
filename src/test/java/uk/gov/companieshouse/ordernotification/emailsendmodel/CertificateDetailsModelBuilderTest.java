package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CertificateDetailsModelBuilderTest {

    private CertificateDetailsModelBuilder builder;

    @BeforeEach
    void setup() {
        builder = new CertificateDetailsModelBuilder();
    }

    @Test
    void correctlyBuildModelWhenListIsEmptyAndBasicInformationNull() {
        builder.includeBasicInformation(null);
        CertificateDetailsModel expected = new CertificateDetailsModel(false, new ArrayList<String>() {
            {
                add("No");
            }
        });
        assertEquals(builder.build(), expected);
    }

    @Test
    void correctlyBuildModelWhenListIsEmptyAndBasicInformationFalse() {
        builder.includeBasicInformation(false);
        CertificateDetailsModel expected = new CertificateDetailsModel(false, new ArrayList<String>() {
            {
                add("No");
            }
        });
        assertEquals(builder.build(), expected);
    }

    @Test
    void correctlyBuildModelWhenListIsEmptyAndBasicInformationTrue() {
        builder.includeBasicInformation(true);

        CertificateDetailsModel expected = new CertificateDetailsModel(false, new ArrayList<String>() {
            {
                add("Yes");
            }
        });
        assertEquals(builder.build(), expected);
    }

    @Test
    void correctlyBuildModelWhenListHasEntriesBasicInformationFalse() {
        builder.includeBasicInformation(false);
        builder.includeText(null, "testNull");
        builder.includeText(false, "testFalse");
        builder.includeText(true, "testTrue");

        CertificateDetailsModel expected = new CertificateDetailsModel(true, new ArrayList<String>() {
            {
                add("testTrue");
            }
        });
        assertEquals(builder.build(), expected);
    }

    @Test
    void correctlyBuildModelWhenListHasEntriesBasicInformationTrue() {
        builder.includeBasicInformation(true);
        builder.includeText(null, "testNull");
        builder.includeText(false, "testFalse");
        builder.includeText(true, "testTrue");

        CertificateDetailsModel expected = new CertificateDetailsModel(true, new ArrayList<String>() {
            {
                add("testTrue");
            }
        });
        assertEquals(builder.build(), expected);
    }
}
