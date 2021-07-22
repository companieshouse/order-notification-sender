package uk.gov.companieshouse.ordernotification.ordersconsumer;

import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.orders.OrderReceived;

import java.util.Arrays;
import java.util.Map;

/**
 * OrderReceived deserializer based on apache kafka Deserializer interface
 * @param <T>
 */
@Component
public class OrderReceivedDeserializer<T extends IndexedRecord> implements Deserializer<T> {
    @SuppressWarnings("unchecked")
    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            Decoder decoder = DecoderFactory.get().binaryDecoder(data, null);
            DatumReader<OrderReceived> reader = new ReflectDatumReader<>(OrderReceived.class);
            return (T)reader.read(null, decoder);
        } catch (Exception e) {
            throw new SerializationException(
                    "Message data [" + Arrays.toString(data) + "] from topic [" + topic + "] cannot be deserialized", e);
        }
    }

    @Override
    public void close() {
        // No-op
    }

    @Override
    public void configure(Map<String, ?> arg0, boolean arg1) {
        // No-op
    }
}