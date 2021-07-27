package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Component
public class DateGenerator {

    public LocalDateTime generate() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
