# Ticket Service

Spring Boot microservice for ticket management with RBAC, Kafka integration, PostgreSQL persistence.

## Prerequisites

- **JDK:** 21 (Maven requires)
- **PostgreSQL:** 15+ (localhost:5432, DB: `ticket_service_db`, user: `postgres`, pass: `root`)
- **Kafka:** 3+ (localhost:9092 with Zookeeper)
- **Maven:** 3.9+ (use `./mvnw` wrapper provided)
- **IDE:** IntelliJ/VSCode with Lombok, Spring Boot plugins
- **Optional:** Docker for Postgres/Kafka

## Local Setup

1. **Clone/Download** project
2. **Start Services:**

   ```bash
   # Postgres (Docker)
   docker run -d --name postgres-ticket -e POSTGRES_DB=ticket_service_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=root -p 5432:5432 postgres:15

   # Kafka (Docker Compose - create docker-compose.yml)
   docker-compose up kafka zookeeper
   ```

3. **Configure** `application.yaml` (JWT secret, DB creds if changed)
4. **Build & Run:**
   ```bash
   ./mvnw clean compile test  # Optional tests
   ./mvnw spring-boot:run     # Port 8082
   ```
5. **Access:**
   - Swagger: http://localhost:8082/swagger-ui.html
   - Actuator: http://localhost:8082/actuator/health
6. **Generate JWT:** External auth service with `roles` claim (e.g. ["ENGINEER"])
7. **Test API** with curl/Postman using Bearer JWT.

## Architecture

- **Framework:** Spring Boot 4.0.3 (Maven)
- **Database:** PostgreSQL with JPA/Hibernate, Liquibase migrations
- **Auth:** JWT (jjwt 0.11), RBAC
- **Messaging:** Spring Kafka producer (`ticket-events` topic)
- **Security:** Spring Security @EnableMethodSecurity
- **Validation:** Bean Validation
- **Docs:** SpringDoc OpenAPI (Swagger)
- **DevTools:** Lombok

**Layers:**

```
Controllers → Services → Repositories → PostgreSQL
                    ↓ (KafkaTemplate)
                Kafka (`ticket-events`)
```

## Role Permissions (RBAC)

| Role     | Permissions                                                           |
| -------- | --------------------------------------------------------------------- |
| ENGINEER | Create tickets (`POST /api/tickets`), Add comments (`POST /comments`) |
| MANAGER  | Assign tickets (`POST /api/tickets/{id}/assign`)                      |
| ADMIN    | Manage categories (CRUD `/api/categories`)                            |

JWT token must include `roles` claim (e.g. `["ENGINEER"]`).

## APIs

### Tickets (`/api/tickets`)

- `POST /` Create (ENGINEER)
- `GET /{id}` Get by ID
- `GET /` List all
- `GET /search` Search
- `GET /statistics` Stats
- `PUT /{id}` Update
- `POST /{id}/assign` Assign (MANAGER)
- `PUT /status` Update status
- `PUT /resolve` Resolve
- `DELETE /{id}` Delete

### Comments (`/comments`)

- `POST /` Add (ENGINEER)
- `GET /` List all

### Contributors (`/contributors`)

- `POST /` Add
- `GET /` List
- `DELETE /{userId}` Remove by user

### Categories (`/api/categories`)

- `POST /` Create (ADMIN)
- `GET /{id}` Get
- `GET /` List
- `PUT /{id}` Update (ADMIN)
- `DELETE /{id}` Delete (ADMIN)

### Others

- Ratings (`/ratings`)
- Internal tickets

## Kafka Events

Kafka producer config: `localhost:9092`, group `ticket-service-group`.

Events produced on:

- Ticket create/update/assign/resolve (check `TicketServiceImpl`)
- Comments added
- Topics: Inferred from service impl (e.g. `ticket-events`).

Sample payload:

```json
{ "eventType": "TICKET_CREATED", "ticketId": "uuid", "data": {...} }
```

## DB Schema

PostgreSQL `ticket_service_db` (user: postgres, pass: root).

**Key Tables (JPA entities):**

- `ticket`: id (UUID PK), title, description, category_id, priority, difficulty, status, assigned_to
- `category`: id (Long PK), name, description
- `ticket_comment`: id, ticket_id, user_id, comment_text
- `ticket_contributor`: ticket_id, user_id
- `ticket_assignment`: ticket_id, user_id
- `ticket_rating`: ticket_id, user_id, rating
- Enums: DifficultyLevel, Priority, Status, Visibility

Liquibase changelogs in `src/main/resources/db/changelog`.

## Local Setup

1. Start PostgreSQL/Kafka (localhost:5432/9092)
2. `./mvnw spring-boot:run`
3. Test: `curl http://localhost:8082/swagger-ui.html`

## CI/CD

**Maven Commands:**

- Build: `./mvnw clean compile`
- Test: `./mvnw test`
- Run: `./mvnw spring-boot:run`
- Package: `./mvnw clean package`

**GitHub Actions (add `.github/workflows/maven.yml`):**

```yaml
name: CI
on: [push, pull_request]
jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4 # JDK 21
        with: { java-version: "21" }
      - run: ./mvnw clean compile test
```

**Deploy:** Dockerize + Kubernetes/Docker Compose for prod.

## Troubleshooting

- JWT secret: `application.yaml`
- Warnings: Unchecked cast in JWT roles (safe).
