package uk.gov.companieshouse.ordernotification.orders.model;

import com.fasterxml.jackson.annotation.JsonValue;

import static uk.gov.companieshouse.ordernotification.orders.model.EnumValueNameConverter.convertEnumValueNameToJson;

public enum DeliveryTimescale {
    STANDARD,
    SAME_DAY;

    @JsonValue
    public String getJsonName() {
        return convertEnumValueNameToJson(this);
    }
}
