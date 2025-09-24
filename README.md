## RAG Chat Storage Microservice (Spring Boot + PostgreSQL)

Production-ready microservice to store RAG chatbot sessions and messages with API key auth, rate limiting, Swagger, health checks, Docker, and pgAdmin.

### Features
- Sessions: create, list, rename, favorite toggle, delete
- Messages: add, list with pagination; optional context payload
- API key auth via `X-API-KEY` header
- Rate limiting (configurable)
- Global error handling and JSON logging
- CORS configuration
- Health checks via Spring Actuator
- Swagger UI at `/swagger-ui/index.html`
- Docker Compose for app + Postgres + pgAdmin

### Requirements
- Java 17+
- Maven 3.8+
- Docker & Docker Compose (for containerized run)

### Configuration
Environment variables (see `.env.example`):
- `API_KEY` (required)
- `DATABASE_URL` (default `jdbc:postgresql://localhost:5432/ragdb`)
- `DB_USERNAME` (default `postgres`)
- `DB_PASSWORD` (default `postgres`)
- `CORS_ALLOWED_ORIGINS` (default `*`)
- `RATE_LIMIT_WINDOW_SECONDS` (default `60`)
- `RATE_LIMIT_MAX_REQUESTS` (default `60`)
- `PORT` (default `8080`)

### Build & Run (Local)
```bash
mvn spring-boot:run
# or
mvn clean package && java -jar target/rag-chat-service-0.0.1-SNAPSHOT.jar
```

### Run with Docker Compose
```bash
docker compose up --build
# App: http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui/index.html
# pgAdmin: http://localhost:5050 (admin@example.com / admin)
```

### API Overview
Base URL: `http://localhost:8080` (requires `X-API-KEY` header)

Sessions
- POST `/api/sessions` { userId, title }
- GET `/api/sessions?userId=...&page=0&size=20`
- PATCH `/api/sessions/{id}/title?userId=...` { title }
- PATCH `/api/sessions/{id}/favorite?userId=...` { favorite }
- DELETE `/api/sessions/{id}?userId=...`

Messages
- POST `/api/sessions/{sessionId}/messages?userId=...` { sender, content, context? }
- GET `/api/sessions/{sessionId}/messages?userId=...&page=0&size=50`

Health & Docs
- GET `/actuator/health`
- GET `/swagger-ui/index.html`

### Notes
- Use UUIDs for session and message IDs.
- `sender` should be `USER` or `ASSISTANT`.
- All endpoints require `X-API-KEY` except health and Swagger.

### Testing
```bash
mvn test
```


