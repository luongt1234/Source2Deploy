# Source2Deploy

**Automated Source Code Analysis, Containerization & Cloud Deployment Platform**

Source2Deploy is a cloud deployment automation platform that allows developers to deploy applications directly from their GitHub repositories without manually configuring Docker, container registries, or cloud infrastructure.

The platform analyzes source code, generates Docker build configurations, builds container images, provisions cloud infrastructure using Terraform, and deploys applications to AWS ECS Fargate.

The goal is to simplify the entire deployment lifecycle into a single workflow.

---

## 1. Project Overview

Traditionally, deploying an application requires developers to perform multiple manual tasks:

- Analyze the application structure and dependencies.
- Write and maintain Dockerfiles.
- Build and manage Docker images.
- Configure container registries.
- Provision cloud infrastructure.
- Configure load balancers and auto-scaling.
- Deploy applications and monitor their health.

Source2Deploy aims to automate these processes.

Developers simply connect a GitHub repository, configure deployment options, and trigger deployment.

```text
GitHub Repository
       |
       v
Source Code Analysis
       |
       v
Dockerfile Generation
       |
       v
Docker Image Build
       |
       v
Push Image to AWS ECR
       |
       v
Infrastructure Provisioning
       |
       v
AWS ECS Fargate Deployment
       |
       v
Application Running
```

## 2. Main Features

### Source Code Analysis
- Connect to GitHub repositories.
- Detect programming languages and frameworks.
- Identify runtime versions and build tools.
- Detect application ports and entry points.
- Generate Docker build configurations.

### Automated Containerization
- Automatically generate Dockerfiles.
- Build Docker images.
- Capture and manage build logs.
- Tag images based on deployment versions.

### Container Registry Management
- Integrate with AWS Elastic Container Registry (ECR).
- Create and manage image repositories.
- Push Docker images to ECR.
- Track image versions and digests.

### Infrastructure as Code
- Provision AWS infrastructure using Terraform.
- Create and manage ECS clusters and services.
- Configure Application Load Balancers (ALB).
- Configure Auto Scaling.
- Manage Terraform state and infrastructure lifecycle.

### Automated Deployment
- Deploy applications to AWS ECS Fargate.
- Configure task definitions and ECS services.
- Monitor deployment progress.
- Perform application health checks.
- Support deployment rollback.

### Monitoring
- Track deployment status.
- Collect build and deployment logs.
- Monitor ECS task health.
- Display deployment information through a dashboard.

## 3. System Architecture

Source2Deploy follows a microservices architecture using Spring Boot.

Each service is responsible for a specific part of the deployment lifecycle. Services communicate asynchronously through Apache Kafka to coordinate deployment tasks.

### Architecture Overview

```text
                  Developer
                      |
                      v
                React Frontend
                      |
                      v
                 API Gateway
                      |
          +-----------+-----------+
          |                       |
          v                       v
    Project Service       Deployment Service
                                  |
                                  v
                            Apache Kafka
                                  |
          +-----------------------+-----------------------+
          |             |           |           |         |
          v             v           v           v         v
      Analyzer        Build      Registry Infrastructure Deploy
      Service        Service     Service     Service    Service
          |             |           |           |         |
          v             v           v           v         v
     Source Code      Docker       AWS ECR   Terraform   AWS ECS
     Analysis         Build                  Provision  Fargate
                                                        |
                                                        v
                                                  Application
```

### Microservices Responsibilities

| Service | Responsibility |
|---|---|
| API Gateway | Request routing and authentication |
| Project Service | Project and GitHub repository management |
| Deployment Service | Deployment orchestration and lifecycle management |
| Analyzer Service | Source code analysis and build configuration generation |
| Build Service | Dockerfile generation and container image building |
| Registry Service | AWS ECR integration and image management |
| Infrastructure Service | Terraform execution and AWS infrastructure provisioning |
| Deploy Service | Application deployment to ECS Fargate |
| Monitoring Service | Deployment logs, status and application monitoring |

## 4. Deployment Workflow

A typical deployment follows these stages:

**Step 1 – Project Registration**

The developer connects a GitHub repository and selects a branch.

**Step 2 – Source Code Analysis**

Analyzer Service examines the repository and identifies the programming language, framework, runtime version, build tool and application port.

**Step 3 – Dockerfile Generation**

Based on the analysis results, the system generates or validates an appropriate Dockerfile.

**Step 4 – Docker Image Build**

Build Service builds the application into a Docker image using an isolated build worker.

**Step 5 – Image Registry**

Registry Service pushes the resulting image to AWS ECR.

**Step 6 – Infrastructure Provisioning**

Infrastructure Service executes Terraform to provision the required AWS resources.

**Step 7 – Application Deployment**

Deploy Service creates or updates ECS Task Definitions and ECS Services.

**Step 8 – Health Check**

The system checks application health and deployment stability before marking the deployment as successful.

## 5. Technology Stack

| Category | Technology |
|---|---|
| Backend | Java, Spring Boot |
| Frontend | React, TypeScript |
| API Gateway | Spring Cloud Gateway |
| Communication | Apache Kafka |
| Database | PostgreSQL |
| Cache | Redis |
| Source Control | GitHub API |
| Containerization | Docker |
| Container Registry | AWS ECR |
| Container Orchestration | AWS ECS Fargate |
| Infrastructure as Code | Terraform |
| Cloud Platform | Amazon Web Services |
| Monitoring | AWS CloudWatch |

## 6. Infrastructure Architecture

Source2Deploy uses AWS services to host and operate deployed applications.

The target deployment architecture includes:

- **Amazon ECR:** Stores Docker images.
- **Amazon ECS:** Manages containerized applications.
- **AWS Fargate:** Runs containers without managing EC2 instances.
- **Application Load Balancer:** Distributes incoming traffic.
- **Auto Scaling:** Automatically adjusts the number of running tasks.
- **Amazon S3:** Stores Terraform state and deployment artifacts.
- **AWS CloudWatch:** Collects logs and metrics.
- **Terraform:** Automates infrastructure provisioning.

## 7. Project Structure

```text
Source2Deploy/
│
├── frontend/
│
├── gateway-service/
│
├── project-service/
│
├── deployment-service/
│
├── analyzer-service/
│
├── build-service/
│
├── registry-service/
│
├── infrastructure-service/
│
├── deploy-service/
│
├── monitoring-service/
│
├── shared/
│   └── event-contracts/
│
├── infrastructure/
│   ├── kafka/
│   ├── docker/
│   └── terraform/
│       ├── modules/
│       └── environments/
│
├── docker-compose.yml
│
└── README.md
```

## 8. Getting Started & Local Development

### Prerequisites
- **Java 21 (JDK 21 LTS)**
- **Apache Maven 3.9+**
- **Docker & Docker Compose**

### Build the Monorepo
To compile all microservices, shared event contracts, and run all unit tests:
```bash
mvn clean test
```

### Run Infrastructure & Microservices Locally
1. Copy the environment template:
   ```bash
   cp .env.example .env
   ```
2. Start the core infrastructure (PostgreSQL multi-databases, Kafka broker, Redis) and all backend microservices:
   ```bash
   docker compose up -d
   ```
3. Verify that the services and health checks are up:
   - **API Gateway**: `http://localhost:8080/actuator/health`
   - **Swagger / OpenAPI**: `http://localhost:8081/swagger-ui.html` (Project Service), `http://localhost:8082/swagger-ui.html` (Deployment Service), etc.

For in-depth details on domain models, event choreography, and Clean Architecture layering, refer to the [Architecture Documentation](docs/architecture.md).

---

## 9. Development Roadmap

The project is planned as a 16-week part-time MVP.

| Phase | Description |
|---|---|
| Phase 1 | Project foundation and GitHub integration |
| Phase 2 | Source code analyzer |
| Phase 3 | Docker image building and AWS ECR integration |
| Phase 4 | AWS ECS Fargate deployment |
| Phase 5 | Terraform infrastructure provisioning |
| Phase 6 | Application Load Balancer and Auto Scaling |
| Phase 7 | Monitoring, testing and documentation |

## 9. Future Improvements

Potential future capabilities include:

- Support for .NET, Node.js and Python applications.
- Kubernetes deployment.
- Google Cloud Run integration.
- Multi-cloud deployment support.
- Custom domain and HTTPS configuration.
- Deployment rollback and version management.
- Advanced monitoring and resource cost estimation.
- GitHub Actions and webhook integration.

## 10. Project Goals

Source2Deploy aims to provide a unified deployment experience where developers can move from source code to a running cloud application with minimal manual infrastructure configuration.

**One Repository. One Configuration. One Click to Deploy.**

---

*Note: Source2Deploy is currently under development. The architecture and features described above represent the intended system design and development roadmap, not a claim that every feature has already been implemented.*