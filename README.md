# Apache Kafka Streams Demo

Apache Kafka Streams streaming data analytics demonstration
using [Streaming Synthetic Sales Data Generator](https://github.com/garystafford/streaming-sales-generator). Outputs
running total of individual drink quantities and total purchases to output Kafka topic.

* Demonstration uses
  Kafka/Flink [Docker Swarm Stack](https://github.com/garystafford/streaming-sales-generator/blob/main/docker-compose.yml)
  from 'Sales Data Generator' project

* Uber JAR built with Gradle using Amazon Corretto (OpenJDK) version 17

## Video Demonstration

Short [YouTube video](https://youtu.be/Hdo4giJePCk) demonstration of this project (video only - no audio).

## Input Topic: Purchases

Sample messages:

```txt
{"transaction_time": "2022-09-09 14:21:34.290884", "product_id": "CS11", "price": 4.99, "quantity": 1, "is_member": true, "member_discount": 0.1, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.49}
{"transaction_time": "2022-09-09 14:21:37.521843", "product_id": "CS05", "price": 4.99, "quantity": 1, "is_member": true, "member_discount": 0.1, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.49}
{"transaction_time": "2022-09-09 14:21:39.769392", "product_id": "CS04", "price": 4.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.99}
{"transaction_time": "2022-09-09 14:21:43.001379", "product_id": "CS03", "price": 4.99, "quantity": 3, "is_member": true, "member_discount": 0.1, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 13.47}
{"transaction_time": "2022-09-09 14:21:46.233837", "product_id": "CS03", "price": 4.99, "quantity": 1, "is_member": true, "member_discount": 0.1, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.49}
{"transaction_time": "2022-09-09 14:21:47.466571", "product_id": "CS05", "price": 4.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.99}
{"transaction_time": "2022-09-09 14:21:49.598899", "product_id": "CS09", "price": 4.99, "quantity": 2, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 9.98}
{"transaction_time": "2022-09-09 14:21:50.828926", "product_id": "CS05", "price": 4.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.99}
{"transaction_time": "2022-09-09 14:21:52.056816", "product_id": "IS03", "price": 5.49, "quantity": 2, "is_member": true, "member_discount": 0.1, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 9.88}
{"transaction_time": "2022-09-09 14:21:55.290320", "product_id": "IS03", "price": 5.49, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 5.49}
```

## Output Topic: Totals

Sample messages:

```txt
{"event_time":"2022-09-09T10:21:32.291510","product_id":"CS10","quantity":3,"total_purchases":14.97}
{"event_time":"2022-09-09T10:21:34.518932","product_id":"CS11","quantity":1,"total_purchases":4.49}
{"event_time":"2022-09-09T10:21:37.769531","product_id":"CS05","quantity":1,"total_purchases":4.49}
{"event_time":"2022-09-09T10:21:39.997495","product_id":"CS04","quantity":4,"total_purchases":21.25}
{"event_time":"2022-09-09T10:21:43.230841","product_id":"CS03","quantity":4,"total_purchases":19.75}
{"event_time":"2022-09-09T10:21:46.464235","product_id":"CS03","quantity":5,"total_purchases":24.24}
{"event_time":"2022-09-09T10:21:47.600491","product_id":"CS05","quantity":2,"total_purchases":9.48}
{"event_time":"2022-09-09T10:21:49.826389","product_id":"CS09","quantity":4,"total_purchases":23.94}
{"event_time":"2022-09-09T10:21:51.054408","product_id":"CS05","quantity":3,"total_purchases":14.47}
{"event_time":"2022-09-09T10:21:52.285675","product_id":"IS03","quantity":4,"total_purchases":22.30}
```

## Commands

### Docker/Kafka

```shell
docker exec -it \
  $(docker container ls --filter  name=kafka-flink_kafka --format "{{.ID}}") \
  bash

export BOOTSTRAP_SERVERS="localhost:9092"
export INPUT_TOPIC="demo.purchases"
export OUTPUT_TOPIC="demo.totals"

# list all topics
kafka-topics.sh --list \
    --bootstrap-server $BOOTSTRAP_SERVERS

# describe a topic
kafka-topics.sh --describe \
    --topic $OUTPUT_TOPIC \
    --bootstrap-server $BOOTSTRAP_SERVERS

# delete topic
kafka-topics.sh --delete \
    --topic $INPUT_TOPIC \
    --bootstrap-server $BOOTSTRAP_SERVERS

kafka-topics.sh --delete \
    --topic $OUTPUT_TOPIC \
    --bootstrap-server $BOOTSTRAP_SERVERS

# optional: create new topic (or they will be automatically created
kafka-topics.sh --create \
    --topic $INPUT_TOPIC \
    --partitions 1 --replication-factor 1 \
    --config cleanup.policy=compact \
    --bootstrap-server $BOOTSTRAP_SERVERS

kafka-topics.sh --create \
    --topic $OUTPUT_TOPIC \
    --partitions 1 --replication-factor 1 \
    --config cleanup.policy=compact \
    --bootstrap-server $BOOTSTRAP_SERVERS

# view messages
kafka-console-consumer.sh \
    --topic $INPUT_TOPIC --from-beginning \
    --bootstrap-server $BOOTSTRAP_SERVERS

kafka-console-consumer.sh \
    --topic $OUTPUT_TOPIC --from-beginning \
    --bootstrap-server $BOOTSTRAP_SERVERS
```

### Java Compile and Run

```shell
# optional - set java version
JAVA_HOME=/Users/garystafford/Library/Java/JavaVirtualMachines/corretto-17.0.3

# compile to uber jar
gradle clean shadowJar

java -cp ../kstreams-kafka-demo/build/libs/kstreams-kafka-demo-1.0-SNAPSHOT-all.jar org.example.Main
```

## References

- <https://github.com/apache/kafka/blob/1.0/streams/examples/src/main/java/org/apache/kafka/streams/examples/pageview/PageViewTypedDemo.java>
- <https://github.com/simplesteph/kafka-streams-course>
- <https://medium.com/@agvillamizar/implementing-custom-serdes-for-java-objects-using-json-serializer-and-deserializer-in-kafka-streams-d794b66e7c03>