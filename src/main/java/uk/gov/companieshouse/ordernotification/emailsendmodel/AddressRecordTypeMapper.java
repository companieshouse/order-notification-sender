package uk.gov.companieshouse.ordernotification.emailsendmodel;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.model.order.item.IncludeAddressRecordsTypeApi;

import java.util.Map;

/**
 * Maps {@link IncludeAddressRecordsTypeApi} objects to human readable strings.
 */
@Component
public class AddressRecordTypeMapper {

    private final Map<IncludeAddressRecordsTypeApi, String> mappings;

    public AddressRecordTypeMapper(@Qualifier("incorporationAddressMappings") Map<IncludeAddressRecordsTypeApi, String> mappings) {
        this.mappings = mappings;
    }

    /**
     * Maps {@link IncludeAddressRecordsTypeApi} objects to human readable strings.
     *
     * @param roaType An enum of registered office address types
     * @return A string representation of the type
     */
    public String mapAddressRecordType(IncludeAddressRecordsTypeApi roaType) {
        return mappings.get(roaType);
    }
}
