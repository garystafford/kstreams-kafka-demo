# Apache Kafka Streams Demo

[Apache Kafka Streams](https://kafka.apache.org/documentation/streams/) streaming data analytics demonstration
using [Streaming Synthetic Sales Data Generator](https://github.com/garystafford/streaming-sales-generator). Consumes a stream of sales transaction messages and publishes a stream of running totals of sales quantities and total purchases to a Kafka topic.

* Demonstration uses
  Kafka/Flink [Docker Swarm Stack](https://github.com/garystafford/streaming-sales-generator/blob/main/docker-compose.yml)
  from 'Sales Data Generator' project

* Uber JAR built with Gradle using Amazon Corretto (OpenJDK) version 17 (openjdk version "17.0.3" 2022-04-19 LTS)

## Video Demonstration

Short [YouTube video](https://youtu.be/Hdo4giJePCk) demonstration of this project (video only - no audio).

## Input Message Stream

Sample sales purchase messages:

```txt
{"transaction_time": "2022-09-09 18:15:14.260159", "product_id": "SF06", "price": 5.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 5.99}
{"transaction_time": "2022-09-09 18:15:17.484820", "product_id": "CS09", "price": 4.99, "quantity": 2, "is_member": true, "member_discount": 0.1, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 8.98}
{"transaction_time": "2022-09-09 18:15:19.711103", "product_id": "CS02", "price": 4.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.99}
{"transaction_time": "2022-09-09 18:15:22.942512", "product_id": "CS05", "price": 4.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.99}
{"transaction_time": "2022-09-09 18:15:26.086557", "product_id": "IS01", "price": 5.49, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 5.49}
{"transaction_time": "2022-09-09 18:15:29.220133", "product_id": "SF03", "price": 5.99, "quantity": 2, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 11.98}
{"transaction_time": "2022-09-09 18:15:32.351425", "product_id": "CS08", "price": 4.99, "quantity": 1, "is_member": true, "member_discount": 0.1, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.49}
{"transaction_time": "2022-09-09 18:15:34.473913", "product_id": "CS05", "price": 4.99, "quantity": 3, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 14.97}
{"transaction_time": "2022-09-09 18:15:35.706493", "product_id": "SC03", "price": 5.99, "quantity": 2, "is_member": true, "member_discount": 0.1, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 10.78}
{"transaction_time": "2022-09-09 18:15:36.938348", "product_id": "CS09", "price": 4.99, "quantity": 1, "is_member": false, "member_discount": 0.0, "add_supplements": false, "supplement_price": 0.0, "total_purchase": 4.99}
```

## Output Message Stream

Sample running product total messages:

```txt
{"event_time":"2022-09-09T14:15:06.444752","product_id":"CS04","quantity":17,"total_purchases":82.12}
{"event_time":"2022-09-09T14:15:07.672817","product_id":"IS02","quantity":32,"total_purchases":180.48}
{"event_time":"2022-09-09T14:15:09.909704","product_id":"CS09","quantity":25,"total_purchases":131.50}
{"event_time":"2022-09-09T14:15:12.034612","product_id":"SF05","quantity":21,"total_purchases":142.08}
{"event_time":"2022-09-09T14:15:13.260653","product_id":"CS09","quantity":26,"total_purchases":135.99}
{"event_time":"2022-09-09T14:15:14.483838","product_id":"SF06","quantity":23,"total_purchases":152.66}
{"event_time":"2022-09-09T14:15:17.706923","product_id":"CS09","quantity":28,"total_purchases":144.97}
{"event_time":"2022-09-09T14:15:19.946669","product_id":"CS02","quantity":21,"total_purchases":114.24}
{"event_time":"2022-09-09T14:15:23.082590","product_id":"CS05","quantity":28,"total_purchases":141.69}
{"event_time":"2022-09-09T14:15:26.214311","product_id":"IS01","quantity":19,"total_purchases":106.64}
```

## Commands

### Java Compile and Run App

```shell
# optional - set java version
JAVA_HOME=~/Library/Java/JavaVirtualMachines/corretto-17.0.3/Contents/Home/

# compile to uber jar
./gradlew clean shadowJar

# run the streaming application
java -cp build/libs/kstreams-kafka-demo-1.0.0-all.jar org.example.Main
```

### Docker Stack

Demonstration uses Kafka/Flink [Docker Swarm Stack](https://github.com/garystafford/streaming-sales-generator/blob/main/docker-compose.yml) from 'Sales Data Generator' project.
  
See [bitnami/kafka](https://hub.docker.com/r/bitnami/kafka) on Docker Hub for more information about running Kafka
locally using Docker.

```shell
# optional: delete previous stack
docker stack rm kafka-flink

# deploy kafka stack
docker swarm init
docker stack deploy kafka-flink --compose-file docker-compose.yml
```

### Docker/Kafka

Helpful Kafka commands.

```shell
docker exec -it $(docker container ls --filter  name=kafka-flink_kafka --format "{{.ID}}") bash

export BOOTSTRAP_SERVERS="localhost:9092"
export INPUT_TOPIC="demo.purchases"
export OUTPUT_TOPIC="demo.totals"

# list all topics
kafka-topics.sh --list \
    --bootstrap-server $BOOTSTRAP_SERVERS

# describe topic
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

## References

- <https://github.com/apache/kafka/blob/1.0/streams/examples/src/main/java/org/apache/kafka/streams/examples/pageview/PageViewTypedDemo.java>
- <https://github.com/simplesteph/kafka-streams-course>
- <https://medium.com/@agvillamizar/implementing-custom-serdes-for-java-objects-using-json-serializer-and-deserializer-in-kafka-streams-d794b66e7c03>

---

_The contents of this repository represent my viewpoints and not of my past or current employers, including Amazon Web Services (AWS). All third-party libraries, modules, plugins, and SDKs are the property of their respective owners. The author(s) assumes no responsibility or liability for any errors or omissions in the content of this site. The information contained in this site is provided on an "as is" basis with no guarantees of completeness, accuracy, usefulness or timeliness._
