package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AddressRecordTypeMapperTest {

    private AddressRecordTypeMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new AddressRecordTypeMapper(Collections.singletonMap(IncludeAddressRecordsTypeApi.CURRENT, "Current"));
    }

    @Test
    void testRetrieveValueMappedToAddressType() {
        //when
        String actual = mapper.mapAddressRecordType(IncludeAddressRecordsTypeApi.CURRENT);

        //then
        assertEquals("Current", actual);
    }

    @Test
    void testReturnNullIfNoValueMappedToAddressType() {
        //when
        String actual = mapper.mapAddressRecordType(IncludeAddressRecordsTypeApi.CURRENT_PREVIOUS_AND_PRIOR);

        //then
        assertNull(actual);
    }
}
