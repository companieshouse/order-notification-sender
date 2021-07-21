package uk.gov.companieshouse.ordernotification.ordersapi.model;

import com.fasterxml.jackson.annotation.JsonValue;

import static uk.gov.companieshouse.ordernotification.ordersapi.converter.EnumValueNameConverter.convertEnumValueNameToJson;


public enum ItemStatus {
    UNKNOWN,
    PROCESSING,
    SATISFIED;

    @JsonValue
    public String getJsonName() {
        return convertEnumValueNameToJson(this);
    }
}
