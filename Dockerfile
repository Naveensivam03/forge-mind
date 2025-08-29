# -----------------------
# Build stage
# -----------------------
FROM eclipse-temurin:24-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and settings
COPY mvnw .
COPY .mvn .mvn

# Copy project files
COPY pom.xml .
COPY src ./src

# Make mvnw executable and build
RUN chmod +x mvnw \
    && ./mvnw clean package -DskipTests

# -----------------------
# Runtime stage
# -----------------------
FROM eclipse-temurin:24-jre-alpine

WORKDIR /app

# Copy the jar from build stage
COPY --from=build /app/target/forge-mind-0.0.1-SNAPSHOT.jar /app/forge-mind-0.0.1-SNAPSHOT.jar
ENV PORT=8080

# Expose the port your app runs on
EXPOSE 8080
# ENTRYPOINT ["java","-jar","forge-mind-0.0.1-SNAPSHOT.jar"]

# Run the application
ENTRYPOINT ["java", "-jar", "forge-mind-0.0.1-SNAPSHOT.jar"]

