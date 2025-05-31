# Use Java 21 as the base image
FROM eclipse-temurin:21-jdk-alpine

# Install necessary packages
RUN apk add --no-cache bash wget

# Create gradle directory and download wrapper
RUN mkdir -p /app/gradle/wrapper && \
    wget -O /app/gradle/wrapper/gradle-wrapper.jar https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.jar && \
    wget -O /app/gradle/wrapper/gradle-wrapper.properties https://raw.githubusercontent.com/gradle/gradle/v8.5.0/gradle/wrapper/gradle-wrapper.properties

# Set working directory
WORKDIR /app

# Copy source code
COPY . .

# Make gradle wrapper executable
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build -x test

# Create directory for credentials and set permissions
RUN mkdir -p /tmp && \
    chmod 777 /tmp

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod \
    SERVER_PORT=8080 \
    SERVER_CONTEXT_PATH=/api \
    JAVA_OPTS="-Xmx512m -Xms256m"

# Run the application
CMD ["java", "-jar", "build/libs/health-service-0.0.1-SNAPSHOT.jar"] 