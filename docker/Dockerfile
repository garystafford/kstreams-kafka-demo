# Gary A. Stafford (2022-12-28)
# does not work with alpine version of base image...

FROM amazoncorretto:17.0.5

COPY build/libs/kstreams-kafka-demo-1.1.0-all.jar /tmp/kstreams-app.jar

CMD ["java", "-jar", "/tmp/kstreams-app.jar"]