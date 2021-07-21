package uk.gov.companieshouse.ordernotification.ordersapi.model;

import com.fasterxml.jackson.annotation.JsonValue;

import static uk.gov.companieshouse.ordernotification.ordersapi.converter.EnumValueNameConverter.convertEnumValueNameToJson;

public enum CertificateType {
    INCORPORATION,
    INCORPORATION_WITH_ALL_NAME_CHANGES,
    INCORPORATION_WITH_LAST_NAME_CHANGES,
    DISSOLUTION;

    @JsonValue
    public String getJsonName() {
        return convertEnumValueNameToJson(this);
    }
}
