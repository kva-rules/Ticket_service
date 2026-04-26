# Ticket Service

Support-ticket lifecycle microservice. Handles ticket CRUD, threaded comments, engineer assignment, contributor tracking, and post-resolution ratings. Emits `ticket.*` Kafka events so reward-service and notification-service react without tight coupling.

---

## At a glance
| | |
|---|---|
| **Port** | 8083 |
| **Database** | postgres-ticket (`ticket_db`) |
| **Kafka topics (out)** | `ticket.created`, `ticket.assigned`, `ticket.resolved`, `ticket.closed` |
| **Kafka topics (in)** | none |
| **Swagger UI (direct)** | http://localhost:8083/swagger-ui.html |
| **Swagger UI (via gateway)** | http://localhost:8080/swagger-ui.html?urls.primaryName=ticket-service |
| **OpenAPI JSON** | http://localhost:8083/v3/api-docs |
| **Java** | 21 (Temurin) |
| **Spring Boot** | 3.3.5 |

---

## What it does
- **Tickets**: create → assign → resolve → close lifecycle with audit trail
- **Comments**: threaded discussion under each ticket
- **Contributors**: multiple engineers can collaborate on one ticket (reward-service uses this to split points)
- **Ratings**: requester scores the engineer after resolution
- **Statistics**: aggregate counts per status/engineer for dashboards
- **Internal lookups** (`/internal/**`): called by reward-service and notification-service to fetch ticket context

---

## API surface

### Tickets (`/api/tickets/**`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/tickets` | JWT | Create a new ticket |
| GET | `/api/tickets` | JWT | List / paginate tickets |
| GET | `/api/tickets/search` | JWT | Full-text + filter search |
| GET | `/api/tickets/statistics` | JWT | Aggregate counts per status/engineer |
| GET | `/api/tickets/{id}` | JWT | Fetch one ticket |
| PUT | `/api/tickets/{id}` | JWT | Update ticket fields |
| PUT | `/api/tickets/{id}/assign` | JWT + ENGINEER | Assign an engineer |
| PUT | `/api/tickets/{id}/status` | JWT | Change status |
| PUT | `/api/tickets/{id}/resolve` | JWT + ENGINEER | Mark resolved → emits `ticket.resolved` |
| DELETE | `/api/tickets/{id}` | JWT + ADMIN | Hard-delete (admins only) |

### Comments (`/api/tickets/{ticketId}/comments`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/tickets/{ticketId}/comments` | JWT | Add a comment |
| GET | `/api/tickets/{ticketId}/comments` | JWT | List comments |

### Contributors (`/api/tickets/{ticketId}/contributors`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/tickets/{ticketId}/contributors` | JWT | Add a contributor |
| GET | `/api/tickets/{ticketId}/contributors` | JWT | List contributors |
| DELETE | `/api/tickets/{ticketId}/contributors/{userId}` | JWT | Remove a contributor |

### Ratings (`/api/tickets/{ticketId}/ratings`)
| Method | Path | Auth | Purpose |
|---|---|---|---|
| POST | `/api/tickets/{ticketId}/ratings` | JWT | Submit resolution rating |

### Internal (`/internal/**`) — service-to-service
| Method | Path | Purpose |
|---|---|---|
| GET | `/internal/tickets/{id}` | Fetch ticket metadata |
| GET | `/internal/tickets/by-user/{userId}` | All tickets owned/assigned to a user |

Live: **http://localhost:8083/swagger-ui.html**.

---

## Configuration
| Env var | Yaml key | Default | Purpose |
|---|---|---|---|
| `SERVER_PORT` | `server.port` | `8083` | |
| `SPRING_DATASOURCE_URL` | | `jdbc:postgresql://postgres-ticket:5432/ticket_db` | |
| `SPRING_DATASOURCE_USERNAME` | | `postgres` | |
| `SPRING_DATASOURCE_PASSWORD` | | `postgres` | |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | | `kafka:9092` | |
| `JWT_SECRET` | `jwt.secret` | (shared) | Must match auth-service |

---

## Kafka events produced
- **`ticket.created`** — on POST, consumed by notification-service
- **`ticket.assigned`** — on assign endpoint, consumed by notification-service
- **`ticket.resolved`** — on resolve endpoint, consumed by reward-service (+50 pts) + notification-service
- **`ticket.closed`** — terminal state

---

## Build & run
```bash
./services.sh start ticket-service
```
or
```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@21/21.0.11
cd Ticket_service
mvn -DskipTests -Dmaven.test.skip=true spring-boot:run
```

## Docker
```bash
docker build -t ticket-service:latest .
docker run --rm -p 8083:8083 ticket-service:latest
```

## Kubernetes
- Manifest: `k8s/ticket-service.yaml`
- Service: `ticket-service`

---

## Troubleshooting

**Tickets not triggering reward points**
Check Kafka: `docker ps | grep kafka`. Then `./services.sh logs reward-service` and confirm the `ticket.resolved` listener is logging consumed messages.

**403 on PUT /api/tickets/{id}**
Only the assigned engineer, a contributor, or ADMIN can update. Check the JWT's role claim via `jwt.io`.

**"Ticket not found" when the ID is correct**
Check you're hitting the right service port (gateway `:8080` vs direct `:8083`) and the ticket_db DB is the one you seeded.

---

## Tech stack
- Java 21 (Temurin)
- Spring Boot 3.3.5
- Spring Security + JJWT (gateway-injected header auth)
- Spring Data JPA + PostgreSQL 16
- Spring Kafka (producer)
- springdoc-openapi 2.6.0
- Lombok 1.18.34, MapStruct 1.6.3
- `com.kva:common-library` 1.0.0
