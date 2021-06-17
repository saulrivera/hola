FROM openjdk:11.0.11-jre

COPY target/tracing-0.0.1-SNAPSHOT.jar /tracing.jar

ENV mongoPassword root
ENV mongoHost localhost
ENV redisHost localhost
ENV neo4jHost bolt://localhost:7687
ENV neo4jUser neo4j
ENV neo4jPass s3cr3t

CMD ["java", "-jar", "/tracing.jar"]