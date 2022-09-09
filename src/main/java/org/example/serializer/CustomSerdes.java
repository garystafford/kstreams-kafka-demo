package org.example.serializer;

// Code source: Arturo González V. - https://medium.com/@agvillamizar/implementing-custom-serdes-for-java-objects-using-json-serializer-and-deserializer-in-kafka-streams-d794b66e7c03

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.example.model.Purchase;
import org.example.model.Total;

public final class CustomSerdes {
    private CustomSerdes() {
    }

    public static Serde<Purchase> Purchase() {
        JsonPojoSerializer<Purchase> serializer = new JsonPojoSerializer<>();
        JsonPojoDeserializer<Purchase> deserializer = new JsonPojoDeserializer<>(Purchase.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }

    public static Serde<Total> Total() {
        JsonPojoSerializer<Total> serializer = new JsonPojoSerializer<>();
        JsonPojoDeserializer<Total> deserializer = new JsonPojoDeserializer<>(Total.class);
        return Serdes.serdeFrom(serializer, deserializer);
    }
}