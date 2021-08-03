package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;

import java.util.Map;

@Component("addressRecordTypeMapper")
public class AddressRecordTypeMapper {

    private Map<IncludeAddressRecordsTypeApi, String> mappings;

    public AddressRecordTypeMapper(@Qualifier("incorporationAddressMappings") Map<IncludeAddressRecordsTypeApi, String> mappings) {
        this.mappings = mappings;
    }

    public String mapAddressRecordType(IncludeAddressRecordsTypeApi roaType) {
        return mappings.get(roaType);
    }
}
