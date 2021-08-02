package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * OrderReceivedNotificationRetry deserializer based on apache kafka Deserializer interface
 * @param <T>
 */
public class MessageDeserialiser<T extends IndexedRecord> implements Deserializer<T> {

    private final Class<T> requiredType;

    public MessageDeserialiser(Class<T> requiredType) {
        this.requiredType = requiredType;
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<T> reader = new ReflectDatumReader<>(requiredType);
            return reader.read(null, decoder);
        } catch (Exception e) {
            return null;
        }
    }
}