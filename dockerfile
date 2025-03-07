FROM openjdk:21-jdk-slim
COPY target/shareit-0.0.1-SNAPSHOT.jar /app/shareit.jar
WORKDIR /app
CMD ["java", "-jar", "shareit.jar"]