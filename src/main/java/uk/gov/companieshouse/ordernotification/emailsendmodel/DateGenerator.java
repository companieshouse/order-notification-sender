package uk.gov.companieshouse.ordernotification.emailsendmodel;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.stereotype.Component;

/**
 * Supply a {@link LocalDateTime} zoned to UTC.
 */
@Component
public class DateGenerator {

    public LocalDateTime generate() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
