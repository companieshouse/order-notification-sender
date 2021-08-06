package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.listener.ConsumerSeekAware;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrdersKafkaConsumerTest {

    @InjectMocks
    private OrdersKafkaConsumer ordersKafkaConsumer;

    @Mock
    private LoggingUtils loggingUtils;

    @Mock
    private Logger logger;

    @Mock
    private ConsumerSeekAware.ConsumerSeekCallback callback;

    @Mock
    private CountDownLatch latch;

    @AfterAll
    static void after() {
        OrdersKafkaConsumer.setLatch(new CountDownLatch(0));
    }

    @Test
    void testThrowRuntimeExceptionIfLatchInterrupted() throws InterruptedException {
        //given
        when(latch.await(anyLong(), any())).thenThrow(InterruptedException.class);
        when(loggingUtils.getLogger()).thenReturn(logger);
        OrdersKafkaConsumer.setLatch(latch);
        ordersKafkaConsumer.setErrorConsumerEnabled(true);

        //when
        Executable actual = () -> ordersKafkaConsumer.onPartitionsAssigned(Collections.emptyMap(), callback);

        //then
        Exception exception = assertThrows(RuntimeException.class, actual);
        verify(logger).error(eq("Interrupted"), any(Exception.class));
    }
}
