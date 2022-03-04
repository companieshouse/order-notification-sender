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
class KindMapperFactoryBuilderTest {
    @Mock
    private KindMapper kindMapper;

    @Test
    @DisplayName("Factory should throw IllegalArgumentException if kind is not recognised")
    void testThrowsIllegalArgumentExceptionIfKindNotHandled() {
        //given
        KindMapperFactory factory = KindMapperFactoryBuilder.newBuilder().build();

        //when
        Executable executable = () -> factory.getInstance("mykind");

        //then
        Exception exception = assertThrows(IllegalArgumentException.class, executable);
        assertThat(exception.getMessage(), is("Unhandled item kind"));
    }

    @Test
    @DisplayName("Factory should return my KindMapper if kind is recognised")
    void testReturnsMyKindMapper() {
        //given
        KindMapperFactory factory = KindMapperFactoryBuilder
                .newBuilder()
                .putKindMapper("mykind", kindMapper)
                .build();

        //when
        KindMapper result = factory.getInstance("mykind");

        //then
        assertThat(result, is(kindMapper));
    }
}
