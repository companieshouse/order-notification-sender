package uk.gov.companieshouse.ordernotification.kafka;


import org.springframework.stereotype.Service;
import uk.gov.companieshouse.kafka.exceptions.SerializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.serialization.AvroSerializer;
import uk.gov.companieshouse.kafka.serialization.SerializerFactory;
import uk.gov.companieshouse.ordernotification.email.EmailSend;
import uk.gov.companieshouse.ordernotification.logging.LoggingUtils;

import java.util.Date;
import java.util.Map;

@Service
public class EmailSendMessageFactory {

	private final SerializerFactory serializerFactory;
	private static final String EMAIL_SEND_TOPIC = "email-send";
	private LoggingUtils loggingUtils;

	public EmailSendMessageFactory(SerializerFactory serializer, LoggingUtils loggingUtils) {
		serializerFactory = serializer;
		this.loggingUtils = loggingUtils;
	}

	/**
	 * Creates an email-send avro message.
	 * @param emailSend email-send object
	 * @return email-send avro message
	 * @throws SerializationException should there be a failure to serialize the EmailSend object
	 */
	public Message createMessage(final EmailSend emailSend, String orderReference) throws SerializationException {
        Map<String, Object> logMap = loggingUtils.createLogMapWithOrderReference(orderReference);
	    logMap.put(LoggingUtils.TOPIC, EMAIL_SEND_TOPIC);
		loggingUtils.getLogger().info("Create kafka message", logMap);
		final AvroSerializer<EmailSend> serializer =
				serializerFactory.getGenericRecordSerializer(EmailSend.class);
		final Message message = new Message();
		message.setValue(serializer.toBinary(emailSend));
		message.setTopic(EMAIL_SEND_TOPIC);
		message.setTimestamp(new Date().getTime());
		loggingUtils.getLogger().info("Kafka message created", logMap);
		return message;
	}
}
