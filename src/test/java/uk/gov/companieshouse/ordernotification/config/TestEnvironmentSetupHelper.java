package uk.gov.companieshouse.ordernotification.config;

public interface TestEnvironmentSetupHelper {

    static void setEnvironmentVariable(String key, String value) {
        System.setProperty(key, value);
    }

}
