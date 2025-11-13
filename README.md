# Weather Sensor Metrics API

This project is a Spring Boot 3 application designed to ingest weather sensor readings and provide statistical insights such as minimum, maximum, sum, and average for selected sensors and metrics. It demonstrates clean backend architecture, proper layering, DTO-based API design, unit and integration testing, and is suitable for coding challenges or real-world microservice foundations.

## 1. Overview

The application exposes two main REST endpoints:
1. To ingest weather sensor data.
2. To query aggregated statistics for selected sensors and metrics.

Each reading includes:
- Sensor ID  
- Metric type (temperature, humidity, wind speed)  
- Metric value  
- Timestamp  

All incoming data is validated and stored in an in-memory H2 database. The service layer performs filtering, grouping, and computation of aggregates based on the query.

## 2. Technology Stack

- Java 17  
- Spring Boot 3 (Web, Validation, Data JPA)  
- H2 in-memory database  
- Maven  
- JUnit 5  

## 3. Project Structure

Base package: `com.example.weather.api`

controller  
- MetricsController

dto  
- MetricIngestionRequest  
- MetricQueryRequest  
- MetricStatisticResponse  

model  
- MetricRecord  

enums  
- MetricType (TEMPERATURE, HUMIDITY, WIND_SPEED)  
- StatisticType (MIN, MAX, SUM, AVG)  

repository  
- MetricRecordRepository  

service  
- MetricIngestionService  
- MetricQueryService  

exception  
- ApiError  
- GlobalExceptionHandler  

## 4. How to Run This Project

### Prerequisites
- Install JDK 17  
- Install Maven 3.8+  
- Ensure JAVA_HOME is set correctly  

### Clone the repository
git clone https://github.com/kumar-souvik-maji/weather-sensor-api.git
cd weather-sensor-api

### Build the project
mvn clean install
### To build without running tests:
mvn clean install -DskipTests
### Run the application
mvn spring-boot:run

5. REST API Details
5.1 Ingest Metric

POST /api/metrics/ingest
Content-Type: application/json
Example:

{
  "sensorId": "sensor-1",
  "metricType": "TEMPERATURE",
  "metricValue": 21.5,
  "timestamp": "2025-11-10T10:15:30Z"
}

5.2 Query Metrics

POST /api/metrics/query
Example:

{
  "sensorIds": ["sensor-1", "sensor-2"],
  "metrics": ["TEMPERATURE", "HUMIDITY"],
  "statistic": "AVG",
  "from": "2025-11-01T00:00:00Z",
  "to": "2025-11-10T23:59:59Z"
}


Example response:

[
  {
    "sensorId": "sensor-1",
    "metricType": "TEMPERATURE",
    "statistic": "AVG",
    "value": 20.75
  }
]


Rules:

sensorIds may be null or empty

metrics must contain at least one metric

Supported statistics: MIN, MAX, SUM, AVG

Date range must be between 1 day and 1 month

If date range is missing, latest data per sensor and metric is used

6. Design and Architecture
Layered Architecture

Controller → Service → Repository → Database

Controller manages HTTP layer and validation

Service handles all business logic

Repository abstracts database interaction

DTO-Based API

DTOs are independent of database entities to maintain API stability.

Statistic Processing

Statistics are computed in the service layer in a clean, easily-extendable design.


