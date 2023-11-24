package uk.gov.companieshouse.ordernotification.consumer.itemgroupprocessedsend;

import java.util.concurrent.CountDownLatch;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
class ItemGroupProcessedSendKafkaConsumerAspect {

    private static CountDownLatch eventLatch = new CountDownLatch(0);

    static void setEventLatch(CountDownLatch eventLatch) {
        ItemGroupProcessedSendKafkaConsumerAspect.eventLatch = eventLatch;
    }

    @Pointcut("execution(public void uk.gov.companieshouse.ordernotification.consumer." +
            "itemgroupprocessedsend.ItemGroupProcessedSendConsumer.processItemGroupProcessedSend(..))")
    public void processOrderReceived() {
    }

    @After("processOrderReceived()")
    void afterProcessOrderReceived() {
        eventLatch.countDown();
    }
}
