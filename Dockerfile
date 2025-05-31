# Use Java 21 as the base image
FROM eclipse-temurin:21-jdk-alpine

# Install necessary packages
RUN apk add --no-cache bash wget

# Set working directory
WORKDIR /app

# Copy source code
COPY . .

# Make gradlew executable and build the application
RUN chmod +x ./gradlew && \
    ./gradlew clean build -x test

# Create directory for credentials
RUN mkdir -p /tmp

# Expose port
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod

# Run the application
CMD ["java", "-jar", "build/libs/healthservice-0.0.1-SNAPSHOT.jar"] 