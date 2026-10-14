package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import java.util.concurrent.CountDownLatch;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
class OrderReceivedKafkaConsumerAspect {

    private CountDownLatch eventLatch = new CountDownLatch(0);

    CountDownLatch getEventLatch() {
        return eventLatch;
    }

    void setEventLatch(CountDownLatch eventLatch) {
        this.eventLatch = eventLatch;
    }

    @Pointcut("execution(public void uk.gov.companieshouse.ordernotification.consumer." +
            "orderreceived.OrderReceivedConsumer.processOrderReceived(..))")
    public void processOrderReceived() {
    }

    @After("processOrderReceived()")
    void afterProcessOrderReceived() {
        eventLatch.countDown();
    }

    @Pointcut("execution(public void uk.gov.companieshouse.ordernotification.consumer." +
            "orderreceived.OrderReceivedConsumer.processOrderReceivedRetry(..))")
    public void processOrderReceivedRetry() {
    }

    @After("processOrderReceivedRetry()")
    void afterProcessOrderReceivedRetry() {
        eventLatch.countDown();
    }
}
