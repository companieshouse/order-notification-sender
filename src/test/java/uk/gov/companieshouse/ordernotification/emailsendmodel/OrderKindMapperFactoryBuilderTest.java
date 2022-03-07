package uk.gov.companieshouse.ordernotification.emailsendmodel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderKindMapperFactoryBuilderTest {
    @Mock
    private OrderKindMapper orderKindMapper;

    @Test
    @DisplayName("Factory should throw IllegalArgumentException if kind is not recognised")
    void testThrowsIllegalArgumentExceptionIfKindNotHandled() {
        //given
        OrderKindMapperFactory factory = OrderKindMapperFactoryBuilder.newBuilder().build();

        //when
        Executable executable = () -> factory.getInstance("my-kind");

        //then
        Exception exception = assertThrows(IllegalArgumentException.class, executable);
        assertThat(exception.getMessage(), is("Unhandled item kind"));
    }

    @Test
    @DisplayName("Factory should return my KindMapper if kind is recognised")
    void testReturnsMyKindMapper() {
        //given
        OrderKindMapperFactory factory = OrderKindMapperFactoryBuilder
                .newBuilder()
                .putKindMapper("my-kind", orderKindMapper)
                .build();

        //when
        OrderKindMapper result = factory.getInstance("my-kind");

        //then
        assertThat(result, is(orderKindMapper));
    }
}
