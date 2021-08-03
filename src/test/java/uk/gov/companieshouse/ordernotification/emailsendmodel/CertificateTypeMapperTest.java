package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.model.order.item.CertificateTypeApi;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CertificateTypeMapperTest {

    private CertificateTypeMapper certificateTypeMapper;

    @BeforeEach
    void setup() {
        certificateTypeMapper = new CertificateTypeMapper(Collections.singletonMap(CertificateTypeApi.INCORPORATION, "Incorporation"));
    }

    @Test
    void testCorrectValueReturnedIfMappingExists() {
        // when
        String actual = certificateTypeMapper.mapCertificateType(CertificateTypeApi.INCORPORATION);

        // then
        assertEquals("Incorporation", actual);
    }

    @Test
    void testNullIsReturnedIfMappingDoesNotExist() {
        // when
        String actual = certificateTypeMapper.mapCertificateType(null);

        // then
        assertNull(actual);
    }
}
