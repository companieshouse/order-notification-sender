package uk.gov.companieshouse.ordernotification.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.orders.OrderReceived;

@Component
public class KafkaTopicInitialiser implements InitializingBean {

    private EmbeddedKafkaBroker broker;
    private KafkaProducer<String, OrderReceived> kafkaProducer;
    private KafkaTopics kafkaTopics;

    @Override
    public void afterPropertiesSet() throws Exception {
        broker.addTopics(new NewTopic(kafkaTopics.getEmailSend(), 1, (short)1));
        broker.addTopics(new NewTopic(kafkaTopics.getOrderReceived(), 1, (short)1));
        broker.addTopics(new NewTopic(kafkaTopics.getOrderReceivedRetry(), 1, (short)1));
        broker.addTopics(new NewTopic(kafkaTopics.getOrderReceivedError(), 1, (short)1));
    }

    @Autowired
    public void setBroker(EmbeddedKafkaBroker broker) {
        this.broker = broker;
    }

    @Autowired
    public void setKafkaProducer(KafkaProducer<String, OrderReceived> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Autowired
    public void setKafkaTopics(KafkaTopics kafkaTopics) {
        this.kafkaTopics = kafkaTopics;
    }
}
