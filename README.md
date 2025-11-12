# weather-sensor-api
# 🌦️ Weather Sensor Metrics API

A small Spring Boot 3 application that ingests weather sensor readings and
returns basic statistics (**min**, **max**, **sum**, **average**) for one or more sensors.

The goal is to show **clean design**, **clear package structure**, and **simple tests**
that are easy to understand even for beginners.

---

## ⚙️ 1. Tech Stack

- ☕ Java 17  
- 🌱 Spring Boot 3 (Web, Validation, Data JPA)  
- 🗄️ H2 In-Memory Database  
- 🧪 JUnit 5 + Spring Boot Test  

---

## 🧩 2. Project Structure

`com.example.weather.api` package:

- **WeatherSensorApiApplication** – main Spring Boot entry class.

### controller
- **MetricsController**
  - `POST /api/metrics/ingest` – ingest one sensor reading.
  - `POST /api/metrics/query` – query statistics.

### dto
- **MetricIngestionRequest** – request body for `/ingest`.  
- **MetricQueryRequest** – request body for `/query`.  
- **MetricStatisticResponse** – one result row per sensor + metric.

### model
- **MetricRecord** – JPA entity for a single reading.

### enums
- **MetricType** – TEMPERATURE, HUMIDITY, WIND_SPEED.  
- **StatisticType** – MIN, MAX, SUM, AVG.

### repository
- **MetricRecordRepository**
  - Uses Spring Data JPA.
  - Custom queries:
    - `findByCriteria(...)` – filter by sensors, metrics, and date range.
    - `findLatestByCriteria(...)` – latest data when no date range is provided.

### service
- **MetricIngestionService** – saves new readings.  
- **MetricQueryService** – loads records, groups by `(sensorId, metricType)`, computes requested statistic.

### exception
- **ApiError** – unified error response.  
- **GlobalExceptionHandler** – maps exceptions to JSON errors.

---

## 🧠 3. Design Choices and Patterns

### Layered Architecture

`Controller → Service → Repository → Database`

- The **Controller** handles HTTP requests and delegates to services.  
- The **Service layer** contains business logic (statistics computation).  
- The **Repository** hides persistence details (H2 today, could be PostgreSQL later).  

This is a standard Spring Boot backend structure, promoting maintainability and testability.

---

### DTO Pattern

We use DTOs (Data Transfer Objects) to decouple API contracts from database entities:

- `MetricIngestionRequest`, `MetricQueryRequest`, `MetricStatisticResponse`  
- Bean Validation (`@NotNull`, `@NotBlank`) ensures clean input data.

---

### Repository Pattern

`MetricRecordRepository` abstracts away the data layer:

- Spring Data JPA generates the implementation.
- Custom `@Query` logic handles filtering and “latest record” behavior.

---

### Statistic Calculation (Strategy-Ready Design)

Currently implemented via a simple `switch` in `MetricQueryService`:

```java
private BigDecimal computeStatistic(List<MetricRecord> records, StatisticType type) {
    // MIN, MAX, SUM, AVG
}

