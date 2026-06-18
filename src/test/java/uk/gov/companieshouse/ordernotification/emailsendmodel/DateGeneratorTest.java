package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class DateGeneratorTest {

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
