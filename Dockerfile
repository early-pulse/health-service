# Use Java 21 as the base image
FROM eclipse-temurin:21-jdk-alpine

# Install necessary packages
RUN apk add --no-cache bash wget

# Set working directory
WORKDIR /app

# Copy Gradle wrapper files first
COPY gradle/wrapper/gradle-wrapper.jar gradle/wrapper/
COPY gradle/wrapper/gradle-wrapper.properties gradle/wrapper/
COPY gradlew .

# Make gradlew executable
RUN chmod +x ./gradlew

# Copy source code
COPY . .

# Build the application
RUN ./gradlew clean build -x test

# Create directory for credentials
RUN mkdir -p /tmp && chmod 777 /tmp

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    SERVER_CONTEXT_PATH=/api \
    JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
CMD ["java", "-jar", "build/libs/healthservice-0.0.1-SNAPSHOT.jar"] 