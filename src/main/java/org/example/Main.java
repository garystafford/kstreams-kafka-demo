package org.example;

// Purpose: Read sales transaction data from a Kafka topic,
//          aggregates product transactions, quantities, and sales on data stream,
//          and writes results to a second Kafka topic.
// Author:  Gary A. Stafford
// Date: 2022-12-28

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Properties props = getProperties();

        kStreamPipeline(props);
    }

    private static Properties getProperties() {
        Properties props = new Properties();

        try (InputStream propsInput =
                     Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            props.load(propsInput);
            return props;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    private static void kStreamPipeline(Properties props) {
        System.out.println("Starting...");

        Properties kafkaStreamsProps = new Properties();
        kafkaStreamsProps.put(StreamsConfig.APPLICATION_ID_CONFIG, props.getProperty("APPLICATION_ID"));
        kafkaStreamsProps.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, props.getProperty("BOOTSTRAP_SERVERS"));
        kafkaStreamsProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, props.getProperty("AUTO_OFFSET_RESET_CONFIG"));
        kafkaStreamsProps.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, props.getProperty("COMMIT_INTERVAL_MS_CONFIG"));

        StreamsBuilder builder = new StreamsBuilder();
        builder
                .stream(props.getProperty("INPUT_TOPIC"), Consumed.with(Serdes.Void(), CustomSerdes.Purchase()))
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
                .to(props.getProperty("OUTPUT_TOPIC"), Produced.with(Serdes.String(), CustomSerdes.Total()));

        //  Topology topology = builder.build();
        // System.out.printf("---> %s%n", topology.describe().toString());

        KafkaStreams streams = new KafkaStreams(builder.build(), kafkaStreamsProps);
        streams.start();
        System.out.println("Running...");
    }
}