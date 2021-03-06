package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum CompanyStatus {
    LIQUIDATION("liquidation");

    private static final Map<String, CompanyStatus> enumValues;

    static {
        enumValues = Arrays.stream(values())
                .collect(Collectors.toMap(CompanyStatus::toString, Function.identity()));
    }

    private final String statusName;

    CompanyStatus(String companyStatus) {
        this.statusName = companyStatus;
    }

    public static CompanyStatus getEnumValue(String companyStatus) {
        return companyStatus != null ? enumValues.get(companyStatus) : null;
    }

    @Override
    public String toString() {
        return statusName;
    }
}