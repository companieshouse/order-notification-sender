package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum Kind {
    CERTIFICATE("item#certificate"),
    CERTIFIED_COPY("item#certified-copy"),
    MISSING_IMAGE_DELIVERY("item#missing-image-delivery");

    private static final Map<String, Kind> enumValues;

    static {
        enumValues = Arrays.stream(values())
                .collect(Collectors.toMap(Kind::toString, Function.identity()));
    }

    private final String kind;

    Kind(String kind) {
        this.kind = kind;
    }

    public static Kind getEnumValue(String kind) {
        return kind != null ? enumValues.get(kind) : null;
    }

    @Override
    public String toString() {
        return kind;
    }
}
