package org.example;

// Purpose: Read sales transaction data from a Kafka topic,
//          aggregates product quantities and total sales on data stream,
//          and writes results to a second Kafka topic.
// Author:  Gary A. Stafford
// Date: 2022-09-07

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
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
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "kstreams-demo-app"); // + LocalDateTime.now().hashCode());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 10); // Used to speed up publishing of messages for demo

        StreamsBuilder builder = new StreamsBuilder();
        builder
                .stream(INPUT_TOPIC, Consumed.with(Serdes.Void(), CustomSerdes.Purchase()))
                .flatMap((KeyValueMapper<Void, Purchase, Iterable<KeyValue<String, Total>>>) (key, value) -> {
                    List<KeyValue<String, Total>> result = new ArrayList<>();
                    result.add(new KeyValue<>(value.getProductId(), new Total(
                            LocalDateTime.now().toString(),
                            value.getProductId(),
                            value.getQuantity(),
                            value.getTotalPurchase()

                    )));
                    return result;
                })
                .groupByKey(Grouped.with(Serdes.String(), CustomSerdes.Total()))
                .reduce((t1, t2) -> {
                    t2.setQuantity(t1.getQuantity() + t2.getQuantity());
                    t2.setTotalPurchases(t1.getTotalPurchases().add(t2.getTotalPurchases()));
                    return t2;
                })
                .toStream()
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), CustomSerdes.Total()));

        // Debugging to console vs publishing to kafka - replace '.to' method above
        // .foreach((key, value) -> System.out.println(key + ": " + value.toString()));

        //  Topology topology = builder.build();
        // System.out.printf("---> %s%n", topology.describe().toString());

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        System.out.println("Running...");
    }
}