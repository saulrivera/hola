FROM  openjdk:11.0.11-jre AS base
WORKDIR /app
EXPOSE 8080

FROM maven:3-openjdk-11 AS build
WORKDIR /src
COPY . .
RUN mvn package

FROM base AS final
WORKDIR /app
COPY --from=build /src/target/tracking.jar .

ENV AWS_PROFILE emr
ENV AWS_REGION us-east-1
ENV AWS_KINESIS_STREAM_NAME tracking-stage-us-east-1-kinesis-data-stream

ENV MONGO_USERNAME root
ENV MONGO_PASSWORD root
ENV MONGO_DATABASE tracing
ENV MONGO_PORT 27017
ENV MONGO_HOST localhost

ENV REDIS_HOST localhost
ENV REDIS_PORT 6379

ENV NEO4J_HOST bolt://localhost:7687
ENV NEO4J_USERNAME neo4j
ENV NEO4J_PASSWORD s3cr3t

ENV BEACON_ALERT_IDENTIFIER 4A854B730C014354A578B238DD26631D

ENTRYPOINT sleep 10 && java -jar /app/tracking.jar
