# Weather Sensor Metrics API

This project is a Spring Boot 3 application designed to ingest weather sensor readings and provide statistical insights such as minimum, maximum, sum, and average for selected sensors and metrics. It follows clean backend architecture, uses DTO-based API models, and applies layered design with proper separation of concerns. The project is suitable for coding assessments and real-world microservice foundations.

## 1. Overview

The application exposes two main REST endpoints:
1. Ingest weather sensor data.
2. Query aggregated statistics for selected sensors and metrics.

Each reading includes:
- Sensor ID
- Metric type (temperature, humidity, wind speed)
- Metric value
- Timestamp

The service validates the input, stores it in an H2 in-memory database, and computes requested statistics using the service layer.

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



Build without running tests:
mvn clean install -DskipTests

Run tests only:
mvn test

### Run the application

Option A – Using Maven:
mvn spring-boot:run


Option B – Using the packaged JAR:
mvn clean package
java -jar target/weather-sensor-api-0.0.1-SNAPSHOT.jar


Application will start at:
http://localhost:8080

##  API Endpoints

###  Ingest metric

POST /api/metrics/ingest

Sample request:

{
  "sensorId": "sensor-1",
  "metricType": "TEMPERATURE",
  "metricValue": 21.5,
  "timestamp": "2025-11-10T10:15:30Z"
}

###  Query statistics

POST /api/metrics/query

Sample request:

{
  "sensorIds": ["sensor-1"],
  "metricTypes": ["TEMPERATURE", "HUMIDITY"],
  "statistic": "AVG",
  "from": "2025-11-10T00:00:00Z",
  "to": "2025-11-11T00:00:00Z"
}





