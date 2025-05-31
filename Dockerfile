# Use Java 21 as the base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Copy Gradle files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src src

# Build the application
RUN ./gradlew clean build -x test

# Expose the port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "build/libs/healthservice-0.0.1-SNAPSHOT.jar"] 