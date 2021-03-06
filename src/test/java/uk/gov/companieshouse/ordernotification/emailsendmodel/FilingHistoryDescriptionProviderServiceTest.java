package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static uk.gov.companieshouse.ordernotification.logging.LoggingUtilsConfiguration.APPLICATION_NAMESPACE;

class FilingHistoryDescriptionProviderServiceTest {
    private static final Map<String, Object> DESCRIPTION_VALUES;
    private static final Map<String, Object> DESCRIPTION_VALUES_LEGACY;
    private static final Map<String, Object> DESCRIPTION_VALUES_ARRAY;

    static {
        DESCRIPTION_VALUES = new HashMap<>();
        DESCRIPTION_VALUES.put("appointment_date", "2010-02-12");
        DESCRIPTION_VALUES.put("officer_name", "The Appointee");

        DESCRIPTION_VALUES_LEGACY = new HashMap<>();
        DESCRIPTION_VALUES_LEGACY.put("description", "This is the description");

        DESCRIPTION_VALUES_ARRAY = new HashMap<>();
        DESCRIPTION_VALUES_ARRAY.put("date", "2019-11-10");
        Map<String, String> capital = new HashMap<>();
        capital.put("figure", "1");
        capital.put("currency", "GBP");
        DESCRIPTION_VALUES_ARRAY.put("capital", capital);
    }

    @Test
    @DisplayName("Replace the values in the description with the values in the descriptionValues, format date and remove any asterisks")
    void mapFilingHistoryDescriptionGetsDescriptionAndReplacesVariables() {
        final FilingHistoryDescriptionProviderService provider = new FilingHistoryDescriptionProviderService(TestConstants.DESCRIPTION_FILE, new LoggingUtils(LoggerFactory.getLogger(APPLICATION_NAMESPACE)), TestConstants.EMAIL_DATE_FORMAT);
        assertThat(provider.mapFilingHistoryDescription(TestConstants.DESCRIPTION_KEY, DESCRIPTION_VALUES), is(TestConstants.EXPECTED_DESCRIPTION));
    }

    @Test
    @DisplayName("Return the description in the descriptionValues if it is present")
    void mapFilingHistoryDescriptionGetsDescriptionAndReturnsDescriptionInDescriptionValue() {
        final FilingHistoryDescriptionProviderService provider = new FilingHistoryDescriptionProviderService(TestConstants.DESCRIPTION_FILE, new LoggingUtils(LoggerFactory.getLogger(APPLICATION_NAMESPACE)), TestConstants.EMAIL_DATE_FORMAT);
        assertThat(provider.mapFilingHistoryDescription(TestConstants.DESCRIPTION_KEY_LEGACY, DESCRIPTION_VALUES_LEGACY), is(TestConstants.EXPECTED_DESCRIPTION_LEGACY));
    }

    @Test
    @DisplayName("Ignore description value if it is not a String")
    void mapFilingHistoryDescriptionIgnoresDescriptionValuesThatAreNotStrings() {
        final FilingHistoryDescriptionProviderService provider = new FilingHistoryDescriptionProviderService(TestConstants.DESCRIPTION_FILE, new LoggingUtils(LoggerFactory.getLogger(APPLICATION_NAMESPACE)), TestConstants.EMAIL_DATE_FORMAT);
        assertThat(provider.mapFilingHistoryDescription(TestConstants.DESCRIPTION_KEY_ARRAY, DESCRIPTION_VALUES_ARRAY), is(TestConstants.EXPECTED_DESCRIPTION_ARRAY));
    }

    @Test
    @DisplayName("Return the description if no descriptionValues are present")
    void mapFilingHistoryDescriptionReturnsDescriptionIfNoDescriptionValuesPresent() {
        final FilingHistoryDescriptionProviderService provider = new FilingHistoryDescriptionProviderService(TestConstants.DESCRIPTION_FILE, new LoggingUtils(LoggerFactory.getLogger(APPLICATION_NAMESPACE)), TestConstants.EMAIL_DATE_FORMAT);
        assertThat(provider.mapFilingHistoryDescription(TestConstants.DESCRIPTION_KEY_NULL, null), is(TestConstants.EXPECTED_DESCRIPTION_NULL));
    }

    @Test
    @DisplayName("Return the description key if no description is present")
    void mapFilingHistoryDescriptionReturnsDescriptionKeyIfNoDescriptionPresent() {
        final FilingHistoryDescriptionProviderService provider = new FilingHistoryDescriptionProviderService(TestConstants.DESCRIPTION_FILE, new LoggingUtils(LoggerFactory.getLogger(APPLICATION_NAMESPACE)), TestConstants.EMAIL_DATE_FORMAT);
        assertThat(provider.mapFilingHistoryDescription("test-key", null), is("test-key"));
    }

    @Test
    @DisplayName("Return null if descriptionKey is null")
    void mapFilingHistoryDescriptionReturnsNullIfDescriptionKeyIsNull() {
        final FilingHistoryDescriptionProviderService provider = new FilingHistoryDescriptionProviderService(TestConstants.DESCRIPTION_FILE, new LoggingUtils(LoggerFactory.getLogger(APPLICATION_NAMESPACE)), TestConstants.EMAIL_DATE_FORMAT);
        assertNull(provider.mapFilingHistoryDescription(null, null));
    }

    @Test
    @DisplayName("Returns null when filing history description file not found")
    void mapFilingHistoryDescriptionFileNotFoundReturnsNull() {
        final FilingHistoryDescriptionProviderService provider = new FilingHistoryDescriptionProviderService("notfound.yaml", new LoggingUtils(LoggerFactory.getLogger(APPLICATION_NAMESPACE)), TestConstants.EMAIL_DATE_FORMAT);
        assertThat(provider.mapFilingHistoryDescription(TestConstants.DESCRIPTION_KEY, DESCRIPTION_VALUES), is(nullValue()));
    }

    @Test
    @DisplayName("Returns null if root property absent from file")
    void mapFilingHistoryDescriptionFileAbsentPropertyReturnsNull() {
        final FilingHistoryDescriptionProviderService provider = new FilingHistoryDescriptionProviderService("missing-key.yaml", new LoggingUtils(LoggerFactory.getLogger(APPLICATION_NAMESPACE)), TestConstants.EMAIL_DATE_FORMAT);
        assertThat(provider.mapFilingHistoryDescription(TestConstants.DESCRIPTION_KEY, DESCRIPTION_VALUES), is(nullValue()));
    }

}
