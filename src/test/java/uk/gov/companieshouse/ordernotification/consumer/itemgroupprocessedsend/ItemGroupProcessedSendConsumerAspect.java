package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import java.util.concurrent.CountDownLatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class ItemGroupProcessedSendConsumerAspect {

    private final CountDownLatch latch;

    public ItemGroupProcessedSendConsumerAspect(CountDownLatch latch) {
        this.latch = latch;
    }

    @After("execution(* uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend."
        + "ItemGroupProcessedSendConsumer.processItemGroupProcessedSend(..))")
    void afterConsume(JoinPoint joinPoint) {
        latch.countDown();
    }

}
