package uk.gov.companieshouse.ordernotification.orders.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Test
    @DisplayName("OrdersApiFields return null if OrdersApi is null")
    void testFieldsReturnNull() {
        //when
        OrdersApiDetails result = OrdersApiDetailsBuilder.newBuilder().build();

        //then
        assertThat(result.getOrdersApi(), is(nullValue()));
        assertThat(result.getBaseItemApi(), is(nullValue()));
        assertThat(result.getKind(), is(nullValue()));
        assertThat(result.getBaseItemOptions(), is(nullValue()));
        assertThat(result.getReference(), is(nullValue()));
    }

    @Test
    @DisplayName("OrdersApiFields return not null if OrdersApi properties set")
    void testFieldsReturnNotNull() {
        when(ordersApi.getItems()).thenReturn(baseItemApis);
        when(baseItemApis.get(0)).thenReturn(baseItemApi);
        when(baseItemApi.getKind()).thenReturn("kind");
        when(baseItemApi.getItemOptions()).thenReturn(baseItemOptionsApi);
        when(ordersApi.getReference()).thenReturn("reference");

        //when
        OrdersApiDetails result = OrdersApiDetailsBuilder.newBuilder()
                .withOrdersApi(ordersApi)
                .build();

        //then
        assertThat(result.getOrdersApi(), is(ordersApi));
        assertThat(result.getBaseItemApi(), is(baseItemApi));
        assertThat(result.getKind(), is("kind"));
        assertThat(result.getBaseItemOptions(), is(baseItemOptionsApi));
        assertThat(result.getReference(), is("reference"));
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
        assertThat(result.getOrdersApi(), is(ordersApi));
        assertThat(result.getBaseItemApi(), is(nullValue()));
    }
}
