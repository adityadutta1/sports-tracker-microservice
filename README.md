# Sports Tracker Microservice

A Java Spring Boot microservice that tracks live sports events by periodically calling external REST APIs and publishing updates to Kafka message broker.


## Setup & Run Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6+ (or use IDE with built-in Maven)
- Docker (optional, for real Kafka)

### Quick Start

1. Clone the repository
   git clone <repository-url>
   cd sports-tracker
   
2. Run the application   
      mvn spring-boot:run
     
    Using IDE
   - Open project in IntelliJ IDEA/VS Code
   - Run `SportsTrackerApplication.java`
  
3. Application starts
      http://localhost:8080
  

The application can run in two modes:

Mock Mode (Default - No external dependencies):
```properties
app.kafka.enabled=false
app.external-api.mock=true
```

**Real Kafka Mode:**
```properties
app.kafka.enabled=true
spring.kafka.bootstrap-servers=localhost:9092
```


If you want to test with real Kafka:

1.Start Kafka with Docker
      docker-compose up -d
   
2. Update application.properties
   ```properties
   app.kafka.enabled=true
   ```

3. Restart application


### Start Event Tracking

curl -X POST http://localhost:8080/events/status \
  -H "Content-Type: application/json" \
  -d '{"eventId": "MATCH001", "status": true}'



### Stop Event Tracking

curl -X POST http://localhost:8080/events/status \
  -H "Content-Type: application/json" \
  -d '{"eventId": "MATCH001", "status": false}'


### Expected Response
```json
{
  "eventId": "MATCH001",
  "status": "live",
  "message": "Status updated successfully"
}
```



### Run All Tests

mvn test


### Run Specific Test Classes

# Context loading test
mvn test -Dtest=SportsTrackerApplicationTests

# API integration test
mvn test -Dtest=SportsTrackerApplicationTests#testEventStatusUpdate



## Design Decisions

### Architecture Overview

REST Client -> EventController-> EventService->ScheduledTaskService->ExternalApiService->KafkaPublisher


### Key Design Choices

1. In-Memory Storage
   - Choice: `ConcurrentHashMap<String, Event>`
   - Reason: Simple, thread-safe, meets MVP requirements
  
2. Scheduling Strategy
   - Choice: `ScheduledExecutorService` with fixed rate execution
   - Reason: Built-in Java, reliable, easy task management
   
3. External API Integration
   - Choice: Mock API for development, configurable for production
   - Reason: No external dependencies, faster development

4. Message Publishing
   - Choice: Configurable Kafka (real/mock modes)
   - Reason: Flexible deployment, development-friendly

5. Error Handling Strategy
   - Choice: Graceful degradation - tasks continue despite errors
   - Reason: Resilient system, no cascading failures
   - Implementation: Try-catch blocks with detailed logging

6. Logging Approach
   - Choice: Structured logging with emojis for visual clarity
   - Levels: DEBUG for detailed flow, INFO for key events, ERROR for failures
