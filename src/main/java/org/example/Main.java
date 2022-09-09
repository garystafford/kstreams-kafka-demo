package org.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Produced;
import org.example.model.Purchase;
import org.example.model.Total;
import org.example.serializer.CustomSerdes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {
    static String BOOTSTRAP_SERVERS = "localhost:9092";
    static String INPUT_TOPIC = "demo.purchases";
    static String OUTPUT_TOPIC = "demo.totals";

    public static void main(String[] args) {
        System.out.println("Starting...");
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "drink-totals-application" + LocalDateTime.now().hashCode());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");


        StreamsBuilder builder = new StreamsBuilder();
        final KStream<Void, Purchase> purchases = builder.stream(
                INPUT_TOPIC, Consumed.with(Serdes.Void(), CustomSerdes.Purchase()));

        purchases.foreach((key, value) -> System.out.println(key + ": " + value.toString()));

        purchases.flatMap(
                (KeyValueMapper<Void, Purchase, Iterable<KeyValue<String, Total>>>) (key, value) -> {
                    List<KeyValue<String, Total>> result = new ArrayList<>();
                    result.add(new KeyValue<>(value.getProductId(), new Total(
                            LocalDateTime.now().toString(),
                            value.getProductId(),
                            value.getQuantity(),
                            value.getTotalPurchase()

                    )));

                    return result;
                }
        ).to(OUTPUT_TOPIC, Produced.with(Serdes.String(), CustomSerdes.Total()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        System.out.println("Stopping...");

    }
}