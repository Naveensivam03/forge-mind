FROM eclipse-temurin:24-jdk-alpine
WORKDIR /app
COPY target/forge-mind-0.0.1-SNAPSHOT.jar /app/forge-mind-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","forge-mind-0.0.1-SNAPSHOT.jar"]
