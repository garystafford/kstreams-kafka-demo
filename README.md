# Kafka Streams Demo

## Commands

### Docker

```shell
export BOOTSTRAP_SERVERS="localhost:9092"
export INPUT_TOPIC="demo.purchases"
export OUTPUT_TOPIC="demo.totals"

kafka-topics.sh --list --bootstrap-server $BOOTSTRAP_SERVERS

kafka-topics.sh --describe \
    --topic $OUTPUT_TOPIC \
    --bootstrap-server $BOOTSTRAP_SERVERS

kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVERS --delete --topic $INPUT_TOPIC
kafka-topics.sh --bootstrap-server $BOOTSTRAP_SERVERS --delete --topic $OUTPUT_TOPIC

kafka-topics.sh --create --topic $INPUT_TOPIC \
    --partitions 1 --replication-factor 1 \
    --config cleanup.policy=compact \
    --bootstrap-server $BOOTSTRAP_SERVERS

kafka-topics.sh --create --topic $OUTPUT_TOPIC \
    --partitions 1 --replication-factor 1 \
    --config cleanup.policy=compact \
    --bootstrap-server $BOOTSTRAP_SERVERS

kafka-console-consumer.sh \
    --bootstrap-server $BOOTSTRAP_SERVERS \
    --topic $INPUT_TOPIC --from-beginning

kafka-console-consumer.sh \
    --bootstrap-server $BOOTSTRAP_SERVERS \
    --topic $OUTPUT_TOPIC --from-beginning
```

### Java

```shell
JAVA_HOME=/Users/garystafford/Library/Java/JavaVirtualMachines/corretto-17.0.3
gradle clean shadowJar
java -cp ../kstreams-kafka-demo/build/libs/kstreams-kafka-demo-1.0-SNAPSHOT-all.jar org.example.Main
```

## References

-<https://github.com/apache/kafka/blob/1.0/streams/examples/src/main/java/org/apache/kafka/streams/examples/pageview/PageViewTypedDemo.java>