package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DateGeneratorTest {

    @Test
    void testGenerateDate() {
        //given
        DateGenerator generator = new DateGenerator();

        //when
        LocalDateTime actual = generator.generate();

        //then
        assertNotNull(actual);
    }
}
