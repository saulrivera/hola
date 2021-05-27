FROM openjdk:11.0.11-jre

COPY target/tracing-0.0.1-SNAPSHOT.jar /tracing.jar

CMD ["java", "-jar", "/tracing.jar"]