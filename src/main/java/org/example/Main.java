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
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.streams.kstream.Produced;
import org.example.model.Purchase;
import org.example.model.Total;
import org.example.serializer.CustomSerdes;

import java.time.Instant;
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
                .peek((unused, purchase) -> System.out.println(purchase.toString()))
                .flatMap((KeyValueMapper<Void, Purchase, Iterable<KeyValue<String, Total>>>) (unused, purchase) -> {
                    List<KeyValue<String, Total>> result = new ArrayList<>();
                    result.add(new KeyValue<>(purchase.getProductId(), new Total(
                            purchase.getTransactionTime(),
                            purchase.getProductId(),
                            1,
                            purchase.getQuantity(),
                            purchase.getTotalPurchase()

                    )));
                    return result;
                })
                .groupByKey(Grouped.with(Serdes.String(), CustomSerdes.Total()))
                .reduce((total1, total2) -> {
                    total2.setTransactions(total1.getTransactions() + total2.getTransactions());
                    total2.setQuantities(total1.getQuantities() + total2.getQuantities());
                    total2.setSales(total1.getSales().add(total2.getSales()));
                    return total2;
                })
                .toStream()
                .peek((productId, total) -> System.out.println(total.toString()))
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), CustomSerdes.Total()));

        //  Topology topology = builder.build();
        // System.out.printf("---> %s%n", topology.describe().toString());

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
        System.out.println("Running...");
    }
}