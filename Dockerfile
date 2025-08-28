# Stage 1: Build
FROM maven:3.9.3-eclipse-temurin-24 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM openjdk:24-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
ENV PORT 8080
EXPOSE ${PORT}
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

