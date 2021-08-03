package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.DeliveryMethodApi;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DeliveryMethodMapperTest {

    private DeliveryMethodMapper mapper;

    @BeforeEach
    void setup() {
        mapper = new DeliveryMethodMapper(Collections.singletonMap(DeliveryMethodApi.POSTAL, "Postal"));
    }

    @Test
    void testReturnValueForMappedDeliveryMethod() {
        //when
        String actual = mapper.mapDeliveryMethod(DeliveryMethodApi.POSTAL);

        //then
        assertEquals(TestConstants.DELIVERY_METHOD, actual);
    }

    @Test
    void testReturnNullForUnmappedDeliveryMethod() {
        //when
        String actual = mapper.mapDeliveryMethod(DeliveryMethodApi.COLLECTION);

        //then
        assertNull(actual);
    }
}
