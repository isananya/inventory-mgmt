FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY target/email-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 9094
ENTRYPOINT ["java", "-jar", "app.jar"]