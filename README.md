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

# Health Service API Documentation

## Base URL
```
http://localhost:8080
```

## Authentication
Currently, the API does not require authentication.

## API Endpoints

### Blood Banks

#### Get All Blood Banks
```
GET /blood-banks
```
Response:
```json
[
    {
        "id": "BB001",
        "name": "City Blood Bank",
        "address": "123 Medical Center Drive, City",
        "phone": "+1234567890",
        "email": "contact@citybloodbank.com",
        "latitude": 32.481596,
        "longitude": -86.4246976,
        "bloodInventory": {
            "A_POSITIVE": 50,
            "B_POSITIVE": 30,
            "O_POSITIVE": 100,
            "AB_POSITIVE": 20,
            "A_NEGATIVE": 25,
            "B_NEGATIVE": 15,
            "O_NEGATIVE": 40,
            "AB_NEGATIVE": 10
        },
        "openingTime": "09:00",
        "closingTime": "18:00",
        "createdAt": "2024-06-14T10:00:00",
        "updatedAt": "2024-06-14T10:00:00"
    }
]
```

#### Get Blood Bank by ID
```
GET /blood-banks/{id}
```

#### Create Blood Bank
```
POST /blood-banks
```
Request Body:
```json
{
    "id": "BB001",
    "name": "City Blood Bank",
    "address": "123 Medical Center Drive, City",
    "phone": "+1234567890",
    "email": "contact@citybloodbank.com",
    "bloodInventory": {
        "A_POSITIVE": 50,
        "B_POSITIVE": 30,
        "O_POSITIVE": 100,
        "AB_POSITIVE": 20,
        "A_NEGATIVE": 25,
        "B_NEGATIVE": 15,
        "O_NEGATIVE": 40,
        "AB_NEGATIVE": 10
    },
    "openingTime": "09:00",
    "closingTime": "18:00"
}
```

#### Update Blood Bank
```
PUT /blood-banks/{id}
```
Request Body: Same as Create Blood Bank

#### Delete Blood Bank
```
DELETE /blood-banks/{id}
```

#### Get Nearby Blood Banks
```
GET /blood-banks/nearby?latitude=32.481596&longitude=-86.4246976&radiusKm=10
```

#### Search Blood Banks
```
GET /blood-banks/search?name=City&bloodType=A_POSITIVE
```

### Doctors

#### Get All Doctors
```
GET /doctors
```
Response:
```json
[
    {
        "id": "DOC001",
        "name": "Dr. John Smith",
        "email": "john.smith@hospital.com",
        "phone": "+1234567890",
        "specialization": "CARDIOLOGY",
        "createdAt": "2024-06-14T10:00:00",
        "updatedAt": "2024-06-14T10:00:00"
    }
]
```

#### Get Doctor by ID
```
GET /doctors/{id}
```

#### Create Doctor
```
POST /doctors
```
Request Body:
```json
{
    "id": "DOC001",
    "name": "Dr. John Smith",
    "email": "john.smith@hospital.com",
    "phone": "+1234567890",
    "specialization": "CARDIOLOGY"
}
```

#### Update Doctor
```
PUT /doctors/{id}
```
Request Body: Same as Create Doctor

#### Delete Doctor
```
DELETE /doctors/{id}
```

#### Get Doctors by Specialization
```
GET /doctors/specialization/{specialization}
```

#### Search Doctors
```
GET /doctors/search?name=John&specialization=CARDIOLOGY
```

### Labs

#### Get All Labs
```
GET /labs
```
Response:
```json
[
    {
        "id": "LAB001",
        "name": "City Diagnostic Lab",
        "address": "123 Medical Center Drive, City",
        "phone": "+1234567890",
        "email": "contact@citylab.com",
        "latitude": 32.481596,
        "longitude": -86.4246976,
        "testNames": ["Blood Test", "X-Ray", "MRI"],
        "openingTime": "09:00",
        "closingTime": "18:00",
        "createdAt": "2024-06-14T10:00:00",
        "updatedAt": "2024-06-14T10:00:00"
    }
]
```

#### Get Lab by ID
```
GET /labs/{id}
```

#### Create Lab
```
POST /labs
```
Request Body:
```json
{
    "id": "LAB001",
    "name": "City Diagnostic Lab",
    "address": "123 Medical Center Drive, City",
    "phone": "+1234567890",
    "email": "contact@citylab.com",
    "testNames": ["Blood Test", "X-Ray", "MRI"],
    "openingTime": "09:00",
    "closingTime": "18:00"
}
```

#### Update Lab
```
PUT /labs/{id}
```
Request Body: Same as Create Lab

#### Delete Lab
```
DELETE /labs/{id}
```

#### Get Nearby Labs
```
GET /labs/nearby?latitude=32.481596&longitude=-86.4246976&radiusKm=10
```

#### Search Labs
```
GET /labs/search?name=City&testName=Blood Test
```

### Appointments

#### Get All Appointments
```
GET /appointments?userId=USER001&entityId=DOC001&page=0&size=10
```

#### Get Appointment by ID
```
GET /appointments/{id}
```

#### Create Appointment
```
POST /appointments
```
Request Body:
```json
{
    "userId": "USER001",
    "entityId": "DOC001",
    "entityType": "DOCTOR",
    "appointmentDateTime": "2024-06-20T10:00:00",
    "reason": "Regular checkup",
    "feedback": "Great service!"
}
```

#### Update Appointment
```
PUT /appointments/{id}
```
Request Body: Same as Create Appointment

#### Update Appointment Status
```
PUT /appointments/{id}/status
```
Request Body:
```json
{
    "status": "CONFIRMED"
}
```

#### Update Appointment Feedback
```
PUT /appointments/{id}/feedback
```
Request Body:
```json
"Great service and very professional staff!"
```

#### Cancel Appointment
```
DELETE /appointments/{id}
```

### Medicines

#### Get All Medicines
```
GET /medicines?page=0&size=10
```

#### Get Medicine by ID
```
GET /medicines/{id}
```

#### Create Medicine
```
POST /medicines
```
Request Body:
```json
{
    "id": "MED001",
    "name": "Paracetamol",
    "category": "PAIN_RELIEF",
    "description": "Pain relief medicine",
    "price": 5.99,
    "manufacturer": "ABC Pharma",
    "expiryDate": "2025-12-31"
}
```

#### Update Medicine
```
PUT /medicines/{id}
```
Request Body: Same as Create Medicine

#### Delete Medicine
```
DELETE /medicines/{id}
```

## Enums

### BloodType
- A_POSITIVE
- B_POSITIVE
- O_POSITIVE
- AB_POSITIVE
- A_NEGATIVE
- B_NEGATIVE
- O_NEGATIVE
- AB_NEGATIVE

### Specialization
- CARDIOLOGY
- NEUROLOGY
- PEDIATRICS
- DERMATOLOGY
- ORTHOPEDICS
- GYNECOLOGY
- OPHTHALMOLOGY
- DENTISTRY

### AppointmentStatus
- PENDING
- CONFIRMED
- CANCELLED
- COMPLETED

### MedicineCategory
- PAIN_RELIEF
- ANTIBIOTICS
- VITAMINS
- ANTACIDS
- ANTIHISTAMINES
- ANTIHYPERTENSIVES
- ANTIDIABETICS
- OTHER

## Error Responses

### 400 Bad Request
```json
{
    "timestamp": "2024-06-14T10:00:00",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid request parameters"
}
```

### 404 Not Found
```json
{
    "timestamp": "2024-06-14T10:00:00",
    "status": 404,
    "error": "Not Found",
    "message": "Resource not found"
}
```

### 500 Internal Server Error
```json
{
    "timestamp": "2024-06-14T10:00:00",
    "status": 500,
    "error": "Internal Server Error",
    "message": "An unexpected error occurred"
}
```

## Request Combinations

### Blood Banks
1. Search by name only:
```
GET /blood-banks/search?name=City
```

2. Search by blood type only:
```
GET /blood-banks/search?bloodType=A_POSITIVE
```

3. Search by both name and blood type:
```
GET /blood-banks/search?name=City&bloodType=A_POSITIVE
```

4. Get nearby blood banks with different radius:
```
GET /blood-banks/nearby?latitude=32.481596&longitude=-86.4246976&radiusKm=5
GET /blood-banks/nearby?latitude=32.481596&longitude=-86.4246976&radiusKm=10
GET /blood-banks/nearby?latitude=32.481596&longitude=-86.4246976&radiusKm=20
```

### Doctors
1. Search by name only:
```
GET /doctors/search?name=John
```

2. Search by specialization only:
```
GET /doctors/specialization/CARDIOLOGY
```

3. Search by both name and specialization:
```
GET /doctors/search?name=John&specialization=CARDIOLOGY
```

### Labs
1. Search by name only:
```
GET /labs/search?name=City
```

2. Search by test name only:
```
GET /labs/search?testName=Blood Test
```

3. Search by both name and test name:
```
GET /labs/search?name=City&testName=Blood Test
```

4. Get nearby labs with different radius:
```
GET /labs/nearby?latitude=32.481596&longitude=-86.4246976&radiusKm=5
GET /labs/nearby?latitude=32.481596&longitude=-86.4246976&radiusKm=10
GET /labs/nearby?latitude=32.481596&longitude=-86.4246976&radiusKm=20
```

### Appointments
1. Get all appointments with pagination:
```
GET /appointments?page=0&size=10
```

2. Get appointments by user:
```
GET /appointments?userId=USER001&page=0&size=10
```

3. Get appointments by entity:
```
GET /appointments?entityId=DOC001&page=0&size=10
```

4. Get appointments by both user and entity:
```
GET /appointments?userId=USER001&entityId=DOC001&page=0&size=10
```

5. Create appointment with different entity types:
```json
// Doctor appointment
{
    "userId": "USER001",
    "entityId": "DOC001",
    "entityType": "DOCTOR",
    "appointmentDateTime": "2024-06-20T10:00:00",
    "reason": "Regular checkup",
    "feedback": "Great service!"
}

// Lab appointment
{
    "userId": "USER001",
    "entityId": "LAB001",
    "entityType": "LAB",
    "appointmentDateTime": "2024-06-20T10:00:00",
    "reason": "Blood test",
    "feedback": "Efficient service!"
}
```

6. Update appointment status:
```json
// Confirm appointment
{
    "status": "CONFIRMED"
}

// Cancel appointment
{
    "status": "CANCELLED"
}

// Complete appointment
{
    "status": "COMPLETED"
}
```

### Medicines
1. Get all medicines with pagination:
```
GET /medicines?page=0&size=10
```

2. Get medicines by category:
```
GET /medicines?category=PAIN_RELIEF&page=0&size=10
```

3. Create medicine with different categories:
```json
// Pain relief medicine
{
    "id": "MED001",
    "name": "Paracetamol",
    "category": "PAIN_RELIEF",
    "description": "Pain relief medicine",
    "price": 5.99,
    "manufacturer": "ABC Pharma",
    "expiryDate": "2025-12-31"
}

// Antibiotic medicine
{
    "id": "MED002",
    "name": "Amoxicillin",
    "category": "ANTIBIOTICS",
    "description": "Antibiotic medicine",
    "price": 15.99,
    "manufacturer": "XYZ Pharma",
    "expiryDate": "2025-12-31"
}
``` 