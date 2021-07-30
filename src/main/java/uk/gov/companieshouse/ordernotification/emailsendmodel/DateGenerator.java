package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * Supply a {@link LocalDateTime} zoned to UTC.
 */
@Component
public class DateGenerator {

    public LocalDateTime generate() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
