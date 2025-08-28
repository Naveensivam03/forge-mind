FROM openjdk:24-jdk-slim

WORKDIR /app

# Copy built JAR
COPY target/forge-mind-0.0.1-SNAPSHOT.jar ./app.jar

# Expose dynamic port for Render
ENV PORT 8080
EXPOSE ${PORT}

# Run the app
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

