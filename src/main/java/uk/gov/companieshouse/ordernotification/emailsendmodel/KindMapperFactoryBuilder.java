package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class KindMapperFactoryBuilder {
    private final Map<String, KindMapper> kindMapperMap = new HashMap<>();

    private KindMapperFactoryBuilder() {
    }

    static KindMapperFactoryBuilder newBuilder() {
        return new KindMapperFactoryBuilder();
    }

    KindMapperFactoryBuilder putKindMapper(String kind, KindMapper kindMapper) {
        this.kindMapperMap.put(kind, kindMapper);
        return this;
    }

    KindMapperFactory build() {
        return new KindMapperFactoryObject(this);
    }

    private static class KindMapperFactoryObject implements KindMapperFactory {
        private final Map<String, KindMapper> kindMapperMap;

        private KindMapperFactoryObject(KindMapperFactoryBuilder builder) {
            this.kindMapperMap = Collections.unmodifiableMap(builder.kindMapperMap);
        }

        @Override
        public KindMapper getInstance(String kind) {
            return Optional.ofNullable(kindMapperMap.get(kind))
                    .orElseThrow(() -> new IllegalArgumentException("Unhandled item kind"));
        }
    }
}
