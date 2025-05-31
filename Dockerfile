# Use Java 21 as the base image
FROM eclipse-temurin:21-jdk-alpine

# Set working directory
WORKDIR /app

# Install necessary packages
RUN apk add --no-cache bash wget

# Copy Gradle files
COPY gradlew .
COPY build.gradle .
COPY settings.gradle .

# Create gradle directory and download wrapper
RUN mkdir -p gradle/wrapper && \
    wget -O gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar && \
    wget -O gradle/wrapper/gradle-wrapper.properties https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.properties

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY src src

# Build the application
RUN ./gradlew clean build -x test

# Create directory for credentials
RUN mkdir -p /tmp

# Expose the port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8080
ENV SERVER_CONTEXT_PATH=/api

# Run the application
CMD ["java", "-jar", "build/libs/healthservice-0.0.1-SNAPSHOT.jar"] 