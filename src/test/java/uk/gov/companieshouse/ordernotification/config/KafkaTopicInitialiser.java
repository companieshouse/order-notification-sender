package uk.gov.companieshouse.ordernotification.config;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.ordernotification.fixtures.TestConstants;
import uk.gov.companieshouse.orders.OrderReceived;

@Component
public class KafkaTopicInitialiser implements InitializingBean {

    private EmbeddedKafkaBroker broker;
    private KafkaProducer<String, OrderReceived> kafkaProducer;

    @Override
    public void afterPropertiesSet() throws Exception {
        broker.addTopics("email-send");
        broker.addTopics("order-received");
        broker.addTopics("order-received-notification-retry");
        broker.addTopics("order-received-notification-error");
        kafkaProducer.send(new ProducerRecord<>("order-received-notification-error", new OrderReceived(TestConstants.ORDER_NOTIFICATION_REFERENCE)));
    }

    @Autowired
    public void setBroker(EmbeddedKafkaBroker broker) {
        this.broker = broker;
    }

    @Autowired
    public void setKafkaProducer(KafkaProducer<String, OrderReceived> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }
}
