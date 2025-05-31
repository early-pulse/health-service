# Health Service

A comprehensive healthcare service platform that provides APIs for managing doctors, appointments, labs, and blood banks. The service includes features like geolocation-based search, appointment scheduling, and blood inventory management.

## Features

- Doctor Management
- Appointment Scheduling
- Lab Management with Location-based Search
- Blood Bank Management with Inventory Tracking
- Google Calendar Integration
- Geolocation Services
- MongoDB Database
- Kafka Integration

## Prerequisites

- Java 21
- MongoDB Atlas Account
- Google Cloud Account (for Calendar and Maps APIs)
- Kafka Server (optional, for event handling)

## Technology Stack

- Spring Boot 3.2.3
- MongoDB
- Spring Data MongoDB
- Spring Kafka
- Google Calendar API
- Google Maps API
- Lombok
- Gradle

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd health-service
```

### 2. Configure Environment Variables
Create a `credentials.json` file in the `src/main/resources` directory with your Google Calendar API credentials.

### 3. Update Application Configuration
Update `src/main/resources/application.yaml` with your MongoDB and other service credentials:

```yaml
spring:
  data:
    mongodb:
      uri: your_mongodb_connection_string
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
    consumer:
      group-id: health-service-group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializer.JsonDeserializer

google:
  calendar:
    credentials-file:
      path: credentials.json
    application-name: early-pulse
  maps:
    api-key: your_google_maps_api_key
```

### 4. Build and Run
```bash
./gradlew build
./gradlew bootRun
```

## API Endpoints

### Doctor Management

#### Get All Doctors
```http
GET /api/doctors?page=0&size=10
```
Response:
```json
{
    "content": [
        {
            "id": "doctor_id",
            "name": "Dr. John Doe",
            "email": "john.doe@example.com",
            "phone": "123-456-7890",
            "specialization": "CARDIOLOGY"
        }
    ],
    "totalElements": 1,
    "totalPages": 1,
    "size": 10,
    "number": 0
}
```

#### Create Doctor
```http
POST /api/doctors
Content-Type: application/json

{
    "name": "Dr. John Doe",
    "email": "john.doe@example.com",
    "phone": "123-456-7890",
    "specialization": "CARDIOLOGY"
}
```

#### Update Doctor
```http
PUT /api/doctors/{id}
Content-Type: application/json

{
    "name": "Dr. John Doe",
    "email": "john.doe@example.com",
    "phone": "123-456-7890",
    "specialization": "CARDIOLOGY"
}
```

#### Delete Doctor
```http
DELETE /api/doctors/{id}
```

### Blood Bank Management

#### Get All Blood Banks
```http
GET /api/blood-banks
```

#### Search Blood Banks by Location
```http
GET /api/blood-banks?latitude=37.4221&longitude=-122.0841&radius=10
```

#### Get Blood Bank by ID
```http
GET /api/blood-banks/{id}
```

#### Create Blood Bank
```http
POST /api/blood-banks
Content-Type: application/json

{
    "name": "City Blood Bank",
    "address": "123 Main St, City",
    "phone": "123-456-7890",
    "email": "contact@citybloodbank.com",
    "bloodInventory": {
        "A_POSITIVE": 50,
        "B_POSITIVE": 30,
        "O_POSITIVE": 100
    },
    "openingTime": "09:00",
    "closingTime": "18:00"
}
```

#### Update Blood Bank
```http
PUT /api/blood-banks/{id}
Content-Type: application/json

{
    "name": "City Blood Bank",
    "address": "123 Main St, City",
    "phone": "123-456-7890",
    "email": "contact@citybloodbank.com",
    "bloodInventory": {
        "A_POSITIVE": 50,
        "B_POSITIVE": 30,
        "O_POSITIVE": 100
    },
    "openingTime": "09:00",
    "closingTime": "18:00"
}
```

#### Delete Blood Bank
```http
DELETE /api/blood-banks/{id}
```

### Lab Management

#### Get All Labs
```http
GET /api/labs
```

#### Search Labs by Location
```http
GET /api/labs?latitude=37.4221&longitude=-122.0841&radius=10
```

#### Get Lab by ID
```http
GET /api/labs/{id}
```

#### Create Lab
```http
POST /api/labs
Content-Type: application/json

{
    "name": "City Lab",
    "address": "123 Main St, City",
    "testNames": ["Blood Test", "X-Ray", "MRI"]
}
```

#### Update Lab
```http
PUT /api/labs/{id}
Content-Type: application/json

{
    "name": "City Lab",
    "address": "123 Main St, City",
    "testNames": ["Blood Test", "X-Ray", "MRI"]
}
```

#### Delete Lab
```http
DELETE /api/labs/{id}
```

### Appointment Management

#### Get All Appointments
```http
GET /api/appointments?page=0&size=10
```

#### Get Appointments by User
```http
GET /api/appointments/user/{userId}?page=0&size=10
```

#### Get Appointments by Doctor
```http
GET /api/appointments/doctor/{doctorId}?page=0&size=10
```

#### Create Appointment
```http
POST /api/appointments
Content-Type: application/json

{
    "userId": "user_id",
    "doctorId": "doctor_id",
    "appointmentDateTime": "2024-03-30T10:00:00"
}
```

#### Update Appointment
```http
PUT /api/appointments/{id}
Content-Type: application/json

{
    "userId": "user_id",
    "doctorId": "doctor_id",
    "appointmentDateTime": "2024-03-30T10:00:00"
}
```

#### Delete Appointment
```http
DELETE /api/appointments/{id}
```

## Testing the APIs

### Using cURL

1. Get all doctors:
```bash
curl -X GET http://localhost:8080/api/doctors
```

2. Create a blood bank:
```bash
curl -X POST http://localhost:8080/api/blood-banks \
  -H "Content-Type: application/json" \
  -d '{
    "name": "City Blood Bank",
    "address": "123 Main St, City",
    "phone": "123-456-7890",
    "email": "contact@citybloodbank.com",
    "bloodInventory": {
        "A_POSITIVE": 50,
        "B_POSITIVE": 30,
        "O_POSITIVE": 100
    },
    "openingTime": "09:00",
    "closingTime": "18:00"
  }'
```

### Using Postman

1. Import the following collection into Postman:
```json
{
  "info": {
    "name": "Health Service API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Doctors",
      "item": [
        {
          "name": "Get All Doctors",
          "request": {
            "method": "GET",
            "url": "http://localhost:8080/api/doctors"
          }
        }
      ]
    },
    {
      "name": "Blood Banks",
      "item": [
        {
          "name": "Get All Blood Banks",
          "request": {
            "method": "GET",
            "url": "http://localhost:8080/api/blood-banks"
          }
        }
      ]
    }
  ]
}
```

## Error Handling

The service includes comprehensive error handling for:
- Resource not found
- Invalid input data
- Database errors
- External service errors (Google Calendar, Maps)

## Security

- The service is configured to use Spring Security (currently disabled)
- API endpoints can be secured by uncommenting the security configuration
- OAuth2 support is available but disabled by default

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support, please open an issue in the GitHub repository or contact the development team. 