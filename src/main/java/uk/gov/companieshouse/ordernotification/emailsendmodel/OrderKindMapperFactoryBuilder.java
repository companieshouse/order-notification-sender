package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class OrderKindMapperFactoryBuilder {
    private final Map<String, OrderKindMapper> kindMapperMap = new HashMap<>();

    private OrderKindMapperFactoryBuilder() {
    }

    static OrderKindMapperFactoryBuilder newBuilder() {
        return new OrderKindMapperFactoryBuilder();
    }

    OrderKindMapperFactoryBuilder putKindMapper(String kind, OrderKindMapper kindMapper) {
        this.kindMapperMap.put(kind, kindMapper);
        return this;
    }

    OrderKindMapperFactory build() {
        return new KindMapperFactoryObject(this);
    }

    private static class KindMapperFactoryObject implements OrderKindMapperFactory {
        private final Map<String, OrderKindMapper> kindMapperMap;

        private KindMapperFactoryObject(OrderKindMapperFactoryBuilder builder) {
            this.kindMapperMap = Collections.unmodifiableMap(builder.kindMapperMap);
        }

        @Override
        public OrderKindMapper getInstance(String kind) {
            return Optional.ofNullable(kindMapperMap.get(kind))
                    .orElseThrow(() -> new IllegalArgumentException("Unhandled item kind"));
        }
    }
}
