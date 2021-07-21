package uk.gov.companieshouse.ordernotification.ordersapi.model;

import com.fasterxml.jackson.annotation.JsonValue;

import static uk.gov.companieshouse.ordernotification.ordersapi.converter.EnumValueNameConverter.convertEnumValueNameToJson;

public enum DeliveryTimescale {
    STANDARD,
    SAME_DAY;

    @JsonValue
    public String getJsonName() {
        return convertEnumValueNameToJson(this);
    }
}
