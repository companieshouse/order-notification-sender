package uk.gov.companieshouse.ordernotification.orders.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.model.order.ActionedByApi;
import uk.gov.companieshouse.api.model.order.OrdersApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemApi;
import uk.gov.companieshouse.api.model.order.item.BaseItemOptionsApi;

@ExtendWith(MockitoExtension.class)
class OrdersApiDetailsBuilderTest {
    @Mock
    private OrdersApi ordersApi;

    @Mock
    private List<BaseItemApi> baseItemApis;

    @Mock
    private BaseItemApi baseItemApi;

    @Mock
    private BaseItemOptionsApi baseItemOptionsApi;

    @Mock
    private ActionedByApi actionedByApi;

    @Test
    @DisplayName("OrdersApiFields return null if OrdersApi is null")
    void testFieldsReturnNull() {
        //when
        OrdersApiDetails result = OrdersApiDetailsBuilder.newBuilder().build();

        //then
        assertThat(result.getKind(), is(nullValue()));
        assertThat(result.getOrderEmail(), is(nullValue()));
        assertThat(result.getOrderReference(), is(nullValue()));
        assertThat(result.getCompanyName(), is(nullValue()));
        assertThat(result.getCompanyNumber(), is(nullValue()));
        assertThat(result.getTotalOrderCost(), is(nullValue()));
        assertThat(result.getPaymentReference(), is(nullValue()));
        assertThat(result.getOrderedAt(), is(nullValue()));
        assertThat(result.getBaseItemApi(), is(nullValue()));
        assertThat(result.getBaseItemOptions(), is(nullValue()));
    }

    @Test
    @DisplayName("OrdersApiFields return not null if OrdersApi properties set")
    void testFieldsReturnNotNull() {
        when(baseItemApi.getKind()).thenReturn("kind");
        when(ordersApi.getOrderedBy()).thenReturn(actionedByApi);
        when(actionedByApi.getEmail()).thenReturn("actionedby-email");
        when(ordersApi.getItems()).thenReturn(baseItemApis);
        when(baseItemApis.get(0)).thenReturn(baseItemApi);
        when(baseItemApi.getItemOptions()).thenReturn(baseItemOptionsApi);
        when(ordersApi.getReference()).thenReturn("order-reference");
        when(baseItemApi.getCompanyName()).thenReturn("company-name");
        when(baseItemApi.getCompanyNumber()).thenReturn("company-number");
        when(ordersApi.getTotalOrderCost()).thenReturn("total-order-cost");
        when(ordersApi.getPaymentReference()).thenReturn("payment-reference");
        when(ordersApi.getOrderedAt()).thenReturn(LocalDateTime.of(2022, 3, 7, 15, 4, 0));

        //when
        OrdersApiDetails result = OrdersApiDetailsBuilder.newBuilder()
                .withOrdersApi(ordersApi)
                .build();

        //then
        assertThat(result.getBaseItemApi(), is(baseItemApi));
        assertThat(result.getKind(), is("kind"));
        assertThat(result.getOrderEmail(), is("actionedby-email"));
        assertThat(result.getBaseItemOptions(), is(baseItemOptionsApi));
        assertThat(result.getOrderReference(), is("order-reference"));
        assertThat(result.getCompanyName(), is("company-name"));
        assertThat(result.getCompanyNumber(), is("company-number"));
        assertThat(result.getTotalOrderCost(), is("total-order-cost"));
        assertThat(result.getPaymentReference(), is("payment-reference"));
        assertThat(result.getOrderedAt(),is(LocalDateTime.of(2022, 3, 7, 15, 4, 0)));
    }

    @Test
    @DisplayName("getBaseItemApi [ordersApi.getItems().get(0)] returns null if OrderApi items list is empty")
    void testBaseItemApiReturnsNull() {
        when(ordersApi.getItems()).thenReturn(baseItemApis);

        //when
        OrdersApiDetails result = OrdersApiDetailsBuilder.newBuilder()
                .withOrdersApi(ordersApi)
                .build();

        //then
        assertThat(result.getBaseItemApi(), is(nullValue()));
    }
}
