package uk.gov.companieshouse.ordernotification.ordersconsumer;

import java.util.concurrent.CountDownLatch;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
class OrdersKafkaConsumerAspect {

    private static CountDownLatch eventLatch = new CountDownLatch(0);

    @Pointcut("execution(public void uk.gov.companieshouse.ordernotification.ordersconsumer" +
            ".OrdersKafkaConsumer.processOrderReceived(..))")
    public void processOrderReceived() {
        eventLatch.countDown();
    }

    @Pointcut("execution(public void uk.gov.companieshouse.ordernotification.ordersconsumer" +
            ".OrdersKafkaConsumer.processOrderReceivedRetry(..))")
    public void processOrderReceivedRetry() {
        eventLatch.countDown();
    }

    static void setEventLatch(CountDownLatch eventLatch) {
        OrdersKafkaConsumerAspect.eventLatch = eventLatch;
    }
}
