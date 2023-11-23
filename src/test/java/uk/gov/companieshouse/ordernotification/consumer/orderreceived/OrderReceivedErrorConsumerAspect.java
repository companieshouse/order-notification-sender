package uk.gov.companieshouse.ordernotification.consumer.orderreceived;

import static java.util.Objects.isNull;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.logging.Logger;

@Aspect
@Component
class OrderReceivedErrorConsumerAspect {
    private CountDownLatch beforeProcessOrderReceivedEventLatch;
    private CountDownLatch afterOrderConsumedEventLatch;
    private final Logger logger;

    @Pointcut("execution(public void uk.gov.companieshouse.ordernotification.consumer." +
            "orderreceived.OrderReceivedErrorConsumer.processOrderReceived(..))")
    void processOrderReceived() {
    }

    @Before("processOrderReceived()")
    void beforeProcessOrderReceived() throws InterruptedException {
        if (!isNull(beforeProcessOrderReceivedEventLatch) && !beforeProcessOrderReceivedEventLatch.await(30, TimeUnit.SECONDS)) {
            logger.debug("pre order consumed latch timed out");
        }
    }

    @After("processOrderReceived()")
    void afterProcessOrderReceived() {
        if (!isNull(afterOrderConsumedEventLatch)) {
            afterOrderConsumedEventLatch.countDown();
        }
    }

    OrderReceivedErrorConsumerAspect(Logger logger) {
        this.logger = logger;
    }

    CountDownLatch getBeforeProcessOrderReceivedEventLatch() {
        return beforeProcessOrderReceivedEventLatch;
    }

    void setBeforeProcessOrderReceivedEventLatch(CountDownLatch countDownLatch) {
        this.beforeProcessOrderReceivedEventLatch = countDownLatch;
    }

    CountDownLatch getAfterOrderConsumedEventLatch() {
        return afterOrderConsumedEventLatch;
    }

    void setAfterOrderConsumedEventLatch(CountDownLatch afterOrderConsumedEventLatch) {
        this.afterOrderConsumedEventLatch = afterOrderConsumedEventLatch;
    }
}