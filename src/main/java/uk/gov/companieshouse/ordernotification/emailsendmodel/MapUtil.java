package uk.gov.companieshouse.ordernotification.emailsendmodel;

final class MapUtil {
    static final String READABLE_FALSE = "No";
    static final String READABLE_TRUE = "Yes";

    static String mapBoolean(Boolean bool) {
        return bool != null && bool ? READABLE_TRUE : READABLE_FALSE;
    }

    private MapUtil() {
    }
}
