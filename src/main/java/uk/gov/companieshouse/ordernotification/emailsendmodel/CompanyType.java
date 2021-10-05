package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.HashMap;
import java.util.Map;

public enum CompanyType {
    LIMITED_PARTNERSHIP("limited-partnership"),
    LIMITED_LIABILITY_PARTNERSHIP("llp");

    private static final Map<String, CompanyType> enumValues = new HashMap<>();

    static {
        for (CompanyType companyType : values()) {
            enumValues.put(companyType.name, companyType);
        }
    }

    private final String name;

    CompanyType(String companyType) {
        this.name = companyType;
    }

    public static CompanyType getEnumValue(String companyType) {
        return enumValues.get(companyType);
    }
}