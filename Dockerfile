# Use Java 21 as the base image
FROM eclipse-temurin:21-jdk-alpine

# Install necessary packages
RUN apk add --no-cache bash wget

# Set working directory
WORKDIR /app

# Download and set up Gradle wrapper
RUN mkdir -p gradle/wrapper && \
    wget -O gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar && \
    wget -O gradle/wrapper/gradle-wrapper.properties https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.properties && \
    wget -O gradlew https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradlew && \
    chmod +x gradlew && \
    chmod +x ./gradlew

# Copy source code
COPY . .

# Build the application
RUN chmod +x ./gradlew && ./gradlew clean build -x test

# Create directory for credentials
RUN mkdir -p /tmp && chmod 777 /tmp

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_CONTEXT_PATH=/api \
    JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
CMD ["java", "-jar", "build/libs/healthservice-0.0.1-SNAPSHOT.jar"] 