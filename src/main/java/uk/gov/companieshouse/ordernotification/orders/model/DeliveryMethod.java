package uk.gov.companieshouse.ordernotification.orders.model;

import com.fasterxml.jackson.annotation.JsonValue;

import static uk.gov.companieshouse.ordernotification.orders.model.EnumValueNameConverter.convertEnumValueNameToJson;

public enum DeliveryMethod {
    POSTAL,
    COLLECTION;

    @JsonValue
    public String getJsonName() {
        return convertEnumValueNameToJson(this);
    }
}
