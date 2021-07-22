package uk.gov.companieshouse.ordernotification.orders.model;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum ItemType {
    CERTIFICATE("item#certificate"),
    CERTIFIED_COPY("item#certified-copy"),
    MISSING_IMAGE_DELIVERY("item#missing-image-delivery");

    private static final Map<String, ItemType> TYPES_BY_KIND;

    private String kind;

    static {
        final Map<String, ItemType> map = new ConcurrentHashMap<>();
        for (final ItemType type: ItemType.values()) {
            map.put(type.getKind(), type);
        }
        TYPES_BY_KIND = Collections.unmodifiableMap(map);
    }

    ItemType(final String kind) {
        this.kind = kind;
    }

    public String getKind() {
        return kind;
    }

    public static ItemType getItemType(final String kind) {
        return TYPES_BY_KIND.get(kind);
    }


}
