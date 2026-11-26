# Source2Deploy Architecture Guide

This document describes the architectural principles, microservice boundaries, domain-driven design, and event-driven communication patterns established for the **Source2Deploy** platform.

---

## 1. System Overview & Monorepo Structure

Source2Deploy is an automated cloud deployment platform built with **Java 21**, **Spring Boot 3.3.5**, and **Spring Cloud**. The codebase follows an industry-standard monorepo layout managed by a centralized root Maven parent POM:

```text
Source2Deploy/
├── pom.xml                               # Root Maven Parent & Dependency Management
├── docker-compose.yml                     # Local orchestration (Kafka, Postgres, Redis, Services)
├── .env.example                          # Environment configuration template
│
├── shared/
│   └── event-contracts/                  # Stable domain event contracts & envelopes
│
├── gateway-service/                      # Spring Cloud Gateway (Port 8080)
├── project-service/                      # Project & GitHub Repository Management (Port 8081)
├── deployment-service/                   # Deployment Orchestration & Workflow (Port 8082)
├── analyzer-service/                     # Source Code Analysis & Tech Stack Detection (Port 8083)
├── build-service/                        # Docker Image Build & Log Streaming (Port 8084)
├── registry-service/                     # AWS ECR Integration & Image Management (Port 8085)
├── infrastructure-service/               # Terraform & AWS Cloud Provisioning (Port 8086)
├── deploy-service/                       # ECS Fargate Deployment & Health Checks (Port 8087)
├── monitoring-service/                   # Deployment Logs & System Observability (Port 8088)
│
├── infrastructure/
│   └── docker/
│       └── init-db.sql                   # Multi-database initializer for PostgreSQL
└── docs/
    └── architecture.md                   # Architecture & DDD documentation
```

---

## 2. Clean Architecture & Layering Rules

Every business microservice strictly enforces Clean Architecture and the Dependency Inversion Principle. Dependencies only point inward:

```text
Presentation Layer (HTTP / REST)
       ↓
Infrastructure Layer (JPA Adapters, Kafka Adapters, Security)
       ↓
Application Layer (Use Cases, Ports, Commands, DTOs)
       ↓
Domain Layer (Pure Java Entities, Value Objects, Repository Interfaces)
```

### Layer Responsibilities:

1. **Domain Layer (`domain/`)**:
   - **Pure Java**: Zero dependencies on Spring Framework, Jakarta Persistence (JPA), or messaging libraries.
   - **Entities & Aggregates**: Encapsulate core business rules, validations, and invariants (e.g., `Deployment`, `Project`, `AnalysisJob`).
   - **Repository Interfaces**: Defined as pure domain interfaces (e.g., `ProjectRepository`), completely decoupled from SQL or JPA.

2. **Application Layer (`application/`)**:
   - **Use Cases & Application Services**: Coordinate domain objects to execute specific user operations (e.g., `CreateProjectUseCase`, `DeployApplicationService`).
   - **Input & Output Ports**: Define contract interfaces for incoming commands and outgoing operations (such as event publishing).
   - **Data Transfer Objects (DTOs)**: Immutable records for cross-layer data transport without leaking domain or entity internals.

3. **Infrastructure Layer (`infrastructure/`)**:
   - **Persistence Adapters**: Implement domain repository interfaces using Spring Data JPA (`ProjectPersistenceAdapter`).
   - **Messaging Producers/Consumers**: Kafka producers and consumers for event-driven coordination.
   - **Configuration**: Spring beans wiring dependencies using constructor injection.
   - **Database Migrations**: Flyway scripts (`V1__init_*.sql`) ensuring versioned, reproducible schemas.

4. **Presentation Layer (`presentation/`)**:
   - **Controllers**: Thin REST endpoints (`@RestController`) that validate incoming DTOs (`@Valid`) and delegate to use cases.
   - **Global Exception Handling**: Centralized `@RestControllerAdvice` mapping exceptions to standard API error responses.
   - **OpenAPI / Swagger**: Auto-generated interactive API documentation at `/swagger-ui.html`.

---

## 3. Kafka Event-Driven Architecture (EDA)

Services communicate asynchronously through Apache Kafka topics following an event-choreography pattern.

### Standard Event Envelope (`DomainEvent<T>`)

All Kafka messages use a standardized, immutable envelope:

```java
public record DomainEvent<T>(
    UUID eventId,
    String eventType,
    UUID aggregateId,
    UUID correlationId,
    Instant occurredAt,
    int version,
    T payload
) {}
```

### End-to-End Choreography Flow

```text
[Developer / API Gateway]
            │
            ▼
    [deployment-service]  ──(DeploymentStartedEvent)──► [analyzer-service]
            ▲                                                   │
            │                                                   ▼
            │◄──────────(AnalysisCompletedEvent)────────────────┘
            │
            ├──(BuildRequestedEvent)──────────────────────────► [build-service]
            ▲                                                   │
            │                                                   ▼
            │◄──────────(BuildCompletedEvent)───────────────────┘
            │
            ├──(InfrastructureRequestedEvent)─────────────────► [infrastructure-service]
            ▲                                                   │
            │                                                   ▼
            │◄──────────(InfrastructureReadyEvent)──────────────┘
            │
            └──(ApplicationDeployRequestedEvent)──────────────► [deploy-service]
                                                                │
                                                                ▼
                                                    (DeploymentCompletedEvent)
                                                                │
                                                                ▼
                                                    [monitoring-service] (Audit)
```

### Centralized Kafka Topics

- `deployment.events`: Lifecycle state transitions (`DeploymentStartedEvent`, `DeploymentCompletedEvent`, `DeploymentFailedEvent`).
- `analysis.events`: Code analysis findings (`AnalysisCompletedEvent`, `AnalysisFailedEvent`).
- `build.events`: Container image build progress (`BuildRequestedEvent`, `BuildCompletedEvent`, `BuildFailedEvent`).
- `registry.events`: Image push status (`ImagePushRequestedEvent`, `ImagePushedEvent`).
- `infrastructure.events`: Terraform state & AWS resources (`InfrastructureRequestedEvent`, `InfrastructureReadyEvent`).
- `deploy.events`: ECS deployments and health check outcomes.
- Corresponding `.DLT` (Dead Letter Topics) for resilient error handling and reprocessing.

---

## 4. Database-per-Service Architecture

To eliminate distributed coupling and avoid shared-state deadlocks, each microservice owns an isolated PostgreSQL database:

| Service | Database | Description |
|---|---|---|
| `project-service` | `project_db` | Projects, GitHub connections, branches |
| `deployment-service` | `deployment_db` | Deployments, execution stages, status logs |
| `analyzer-service` | `analyzer_db` | Analysis jobs, language/framework detection results |
| `build-service` | `build_db` | Build worker jobs, duration, image tags |
| `registry-service` | `registry_db` | ECR repository metadata, image digests |
| `infrastructure-service` | `infrastructure_db` | Terraform state references, cluster & ALB ARNs |
| `deploy-service` | `deploy_db` | Application releases, live URLs, rollback states |
| `monitoring-service` | `monitoring_db` | Centralized deployment progress and audit logs |

**Local Setup**:
During local development, a single PostgreSQL 16 container initializes all 8 individual databases automatically via `infrastructure/docker/init-db.sql`.

---

## 5. Security & Observability

- **API Gateway**: Spring Cloud Gateway acts as the secure entry point handling CORS, route filtering, and JWT token authentication.
- **Traceability (`X-Correlation-ID`)**: Every incoming HTTP request is assigned a unique Correlation ID at the gateway. This ID is passed to downstream HTTP headers and serialized within the `DomainEvent` envelope across Kafka topics.
- **Health Checks & Actuator**: Spring Boot Actuator is exposed on all services at `/actuator/health` with Kubernetes/Docker liveness and readiness probe compatibility.
- **Virtual Threads**: Java 21 Virtual Threads (`spring.threads.virtual.enabled: true`) are enabled across all services for high-throughput, non-blocking thread scheduling.
