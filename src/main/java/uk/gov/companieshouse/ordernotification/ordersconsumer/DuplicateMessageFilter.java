package uk.gov.companieshouse.ordernotification.ordersconsumer;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.springframework.messaging.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.orders.OrderReceived;

/**
 * Records OrderReceived messages and filters messages supplied with the same URI and attempt properties.
 *
 * <p>Note: Supplied messages are aged out of the internal cache on a least recently used basis.</p>
 */
public class DuplicateMessageFilter implements MessageFilter<OrderReceived> {
    final Set<CacheEntry> cache;
    final Logger logger;

    public DuplicateMessageFilter(int cacheSize, Logger logger) {
        cache = Collections.newSetFromMap(new LinkedHashMap<CacheEntry, Boolean>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<CacheEntry, Boolean> eldest) {
                return size() > cacheSize;
            }
        });
        this.logger = logger;
    }

    @Override
    public synchronized boolean include(Message<OrderReceived> message) {
        OrderReceived orderReceived = message.getPayload();
        CacheEntry cacheEntry = new CacheEntry(orderReceived);

        boolean include = !cache.contains(cacheEntry);
        if (include) {
            cache.add(cacheEntry);
        } else {
            logger.debug(String.format("'order-received' message is a duplicate: uri = '%s', attempt = '%d'",
                    cacheEntry.uri,
                    cacheEntry.attempt));
        }

        return include;
    }

    private static class CacheEntry {
        private final String uri;
        private final int attempt;

        CacheEntry(OrderReceived orderReceived) {
            this.uri = orderReceived.getOrderUri();
            this.attempt = orderReceived.getAttempt();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheEntry that = (CacheEntry) o;
            return attempt == that.attempt && Objects.equals(uri, that.uri);
        }

        @Override
        public int hashCode() {
            return Objects.hash(uri, attempt);
        }
    }
}