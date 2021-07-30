package uk.gov.companieshouse.ordernotification.messageproducer;

import org.apache.avro.generic.GenericRecord;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.Date;
import java.util.Map;

@Service
class MessageFactory {

	private final SerializerFactory serializerFactory;
	private LoggingUtils loggingUtils;

	public MessageFactory(SerializerFactory serializer, LoggingUtils loggingUtils) {
		serializerFactory = serializer;
		this.loggingUtils = loggingUtils;
	}

	/**
	 * Creates an email-send avro message.
	 * @param record record
	 * @return email-send avro message
	 * @throws SerializationException should there be a failure to serialize the EmailSend object
	 */
	public Message createMessage(final GenericRecord record, String orderReference, String topic) throws SerializationException {
        Map<String, Object> logMap = loggingUtils.createLogMapWithOrderReference(orderReference);
	    logMap.put(LoggingUtils.TOPIC, topic);
		loggingUtils.getLogger().info("Create kafka message", logMap);
		final AvroSerializer<GenericRecord> serializer =
				serializerFactory.getGenericRecordSerializer(GenericRecord.class);
		final Message message = new Message();
		message.setValue(serializer.toBinary(record));
		message.setTopic(topic);
		message.setTimestamp(new Date().getTime());
		loggingUtils.getLogger().info("Kafka message created", logMap);
		return message;
	}
}
