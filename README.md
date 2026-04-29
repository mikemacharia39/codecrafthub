# CodeCraftHub

A Spring Boot REST API for managing learning courses. Courses are persisted to a local JSON file, making this a lightweight, zero-database setup suitable for development and personal use.

## Tech Stack

- **Java 21**
- **Spring Boot 4.0.5** (Web MVC, Validation)
- **Jackson** (JSON serialization with Java 8 date/time support)
- **Lombok** (boilerplate reduction)
- **JUnit 5** (testing)
- **Gradle** (build tool)

## Getting Started

### Prerequisites

- JDK 21+
- Gradle (or use the included `./gradlew` wrapper)

### Run the application

```bash
./gradlew bootRun
```

The server starts on `http://localhost:8080`.

Course data is persisted to `/tmp/courses.json` and loaded automatically on startup.

### Run tests

```bash
./gradlew test
```

## API Reference

Base path: `/api/courses`

| Method | Endpoint            | Description           | Request Body       | Response         |
|--------|---------------------|-----------------------|--------------------|------------------|
| GET    | `/api/courses`      | List all courses      | —                  | `200 OK`         |
| GET    | `/api/courses/{id}` | Get a course by ID    | —                  | `200 OK`         |
| POST   | `/api/courses`      | Create a new course   | `CourseRequestDTO` | `201 Created`    |
| PUT    | `/api/courses/{id}` | Update a course by ID | `CourseRequestDTO` | `200 OK`         |
| DELETE | `/api/courses/{id}` | Delete a course by ID | —                  | `204 No Content` |

### Request body (`CourseRequestDTO`)

```json
{
  "name": "Spring Boot Fundamentals",
  "description": "Learn the core concepts of Spring Boot",
  "target_date": "2026-12-31",
  "status": "In Progress"
}
```

### Response body (`CourseResponseDTO`)

```json
{
  "id": 1,
  "name": "Spring Boot Fundamentals",
  "description": "Learn the core concepts of Spring Boot",
  "target_date": "2026-12-31",
  "status": "IN_PROGRESS",
  "created_at": "2026-04-29T10:00:00"
}
```

### Course statuses

| Value         | Meaning                        |
|---------------|--------------------------------|
| `NOT_STARTED` | Course has not been started    |
| `IN_PROGRESS` | Course is actively in progress |
| `COMPLETED`   | Course has been completed      |

### Error responses

| Status | Scenario                              |
|--------|---------------------------------------|
| `400`  | Invalid or missing request fields     |
| `404`  | Course not found for the given ID     |
| `500`  | Failed to read from or write to file  |

## Project Structure

```
src/main/java/com/mikehenry/codecrafthub/
├── CodecrafthubApplication.java   # Application entry point
├── configuration/
│   └── JsonConfig.java            # Jackson ObjectMapper configuration
├── controller/
│   └── CourseController.java      # REST endpoints
├── dto/
│   ├── CourseRequestDTO.java      # Incoming request payload
│   └── CourseResponseDTO.java     # Outgoing response payload
├── enums/
│   └── CourseStatus.java          # Allowed course status values
├── exception/
│   └── GlobalExceptionHandler.java # Centralized error handling
├── model/
│   └── Course.java                # Course entity
└── service/
    └── CourseService.java         # Business logic and file persistence
```

## Data Persistence

Course data is stored as a JSON array in `/tmp/courses.json`. The file is created automatically on first startup. IDs are auto-incremented and the generator is seeded from the highest existing ID on startup to prevent collisions after restarts.
