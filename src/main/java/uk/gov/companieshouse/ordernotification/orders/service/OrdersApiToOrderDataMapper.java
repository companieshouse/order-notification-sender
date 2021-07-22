package uk.gov.companieshouse.ordernotification.orders.service;

import org.mapstruct.AfterMapping;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertificateItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.CertifiedCopyItemOptionsApi;
import uk.gov.companieshouse.api.model.order.item.MissingImageDeliveryItemOptionsApi;
import uk.gov.companieshouse.ordernotification.orders.model.CertificateItemOptions;
import uk.gov.companieshouse.ordernotification.orders.model.CertifiedCopyItemOptions;
import uk.gov.companieshouse.ordernotification.orders.model.Item;
import uk.gov.companieshouse.ordernotification.orders.model.ItemOptions;
import uk.gov.companieshouse.ordernotification.orders.model.ItemType;
import uk.gov.companieshouse.ordernotification.orders.model.MissingImageDeliveryItemOptions;
import uk.gov.companieshouse.ordernotification.orders.model.OrderData;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
interface OrdersApiToOrderDataMapper {
    OrderData ordersApiToOrderData(OrdersApi ordersApi);

    @Mapping(source = "links.self", target="itemUri")
    @Mapping(target = "satisfiedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Item apiToItem(BaseItemApi baseItemApi);

    /**
     * Maps item_options based on description_identifier correctly to
     * {@link CertificateItemOptions} or {@link CertifiedCopyItemOptions}
     * @param baseItemApi item object received via api call
     * @param item item object to be constructed from item received via api call
     */
    @AfterMapping
    default void apiToItemOptions(BaseItemApi baseItemApi, @MappingTarget Item item) {
        final String itemKind = baseItemApi.getKind();
        final BaseItemOptionsApi baseItemOptionsApi = baseItemApi.getItemOptions();
        if (itemKind.equals(ItemType.CERTIFICATE.getKind())) {
            item.setItemOptions(apiToCertificateItemOptions((CertificateItemOptionsApi) baseItemOptionsApi));
        }
        else if (itemKind.equals(ItemType.CERTIFIED_COPY.getKind())){
            item.setItemOptions(apiToCertifiedCopyItemOptions((CertifiedCopyItemOptionsApi) baseItemOptionsApi));
        } else {
            item.setItemOptions(apiToMissingImageDeliveryOptions((MissingImageDeliveryItemOptionsApi) baseItemOptionsApi));
        }
    }

    ItemOptions apiToOptions(BaseItemOptionsApi baseItemOptionsApi);
    CertificateItemOptions apiToCertificateItemOptions(CertificateItemOptionsApi certificateItemOptionsApi);
    CertifiedCopyItemOptions apiToCertifiedCopyItemOptions(CertifiedCopyItemOptionsApi certifiedCopyItemOptionsApi);
    MissingImageDeliveryItemOptions apiToMissingImageDeliveryOptions(MissingImageDeliveryItemOptionsApi missingImageDeliveryItemOptionsApi);
}
