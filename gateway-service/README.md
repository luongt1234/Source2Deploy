# Gateway Service (Source2Deploy)

API Gateway Service cho nền tảng **Source2Deploy**, xây dựng bằng **Spring Boot 3.3.5**, **Spring Cloud Gateway (Reactive)** và **Java 21**.

---

## 1. Tính năng nổi bật

- **Java 21 Ready**: Tận dụng tính năng Virtual Threads (`spring.threads.virtual.enabled: true`) và ZGC tối ưu hóa hiệu năng.
- **Spring Cloud Gateway (WebFlux / Netty)**: Kiến trúc non-blocking I/O có khả năng chịu tải cao.
- **Centralized Routing**: Điều hướng request tới các microservices trong hệ sinh thái Source2Deploy:
  - `project-service` (`/api/v1/projects/**`)
  - `deployment-service` (`/api/v1/deployments/**`)
  - `analyzer-service` (`/api/v1/analyzer/**`)
  - `build-service` (`/api/v1/builds/**` và WebSocket `/ws/builds/**`)
  - `registry-service` (`/api/v1/registry/**`)
  - `infrastructure-service` (`/api/v1/infra/**`)
  - `deploy-service` (`/api/v1/deploy/**`)
  - `monitoring-service` (`/api/v1/monitoring/**` và WebSocket `/ws/monitoring/**`)
- **Global Logging & Distributed Tracing**: Bộ lọc `LoggingGlobalFilter` tự động cấp phát hoặc chuyển tiếp `X-Correlation-ID`, ghi log thời gian xử lý của mỗi request.
- **Custom Auth Filter**: `AuthValidationGatewayFilterFactory` sẵn sàng kiểm tra JWT token.
- **CORS Configured**: Hỗ trợ tích hợp sẵn với React/Vite frontend (`localhost:3000`, `localhost:5173`).
- **Resilience**: Tự động Retry cho GET requests khi gặp lỗi mạng/timeout.
- **Production-ready Dockerfile**: Multi-stage build với Alpine Linux, non-root user và ZGC tuning.

---

## 2. Cấu trúc thư mục

```text
gateway-service/
├── src/
│   ├── main/
│   │   ├── java/com/source2deploy/gateway/
│   │   │   ├── GatewayServiceApplication.java
│   │   │   ├── controller/
│   │   │   │   └── FallbackController.java
│   │   │   └── filter/
│   │   │       ├── LoggingGlobalFilter.java
│   │   │       └── AuthValidationGatewayFilterFactory.java
│   │   └── resources/
│   │       └── application.yml
│   └── test/
│       └── java/com/source2deploy/gateway/
│           └── GatewayServiceApplicationTests.java
├── Dockerfile
├── .dockerignore
├── .gitignore
├── pom.xml
└── README.md
```

---

## 3. Các biến môi trường (Environment Variables)

Bạn có thể tuỳ biến các URL đích thông qua biến môi trường khi triển khai:

| Biến môi trường | Mặc định | Mô tả |
|---|---|---|
| `SERVER_PORT` | `8080` | Cổng HTTP lắng nghe của Gateway |
| `PROJECT_SERVICE_URL` | `http://localhost:8081` | Địa chỉ Project Service |
| `DEPLOYMENT_SERVICE_URL` | `http://localhost:8082` | Địa chỉ Deployment Service |
| `ANALYZER_SERVICE_URL` | `http://localhost:8083` | Địa chỉ Analyzer Service |
| `BUILD_SERVICE_URL` | `http://localhost:8084` | Địa chỉ Build Service |
| `BUILD_SERVICE_WS_URL` | `ws://localhost:8084` | WebSocket URL cho Build Logs |
| `REGISTRY_SERVICE_URL` | `http://localhost:8085` | Địa chỉ Registry Service |
| `INFRASTRUCTURE_SERVICE_URL` | `http://localhost:8086` | Địa chỉ Infrastructure Service |
| `DEPLOY_SERVICE_URL` | `http://localhost:8087` | Địa chỉ Deploy Service |
| `MONITORING_SERVICE_URL` | `http://localhost:8088` | Địa chỉ Monitoring Service |
| `MONITORING_SERVICE_WS_URL` | `ws://localhost:8088` | WebSocket URL cho Monitoring |

---

## 4. Hướng dẫn chạy

### Chạy trực tiếp bằng Maven:
```bash
mvn clean spring-boot:run
```

### Build và chạy với Docker:
```bash
# Build image
docker build -t source2deploy-gateway:latest .

# Run container
docker run -p 8080:8080 --name gateway-service source2deploy-gateway:latest
```

---

## 5. Endpoints hữu ích

- **Actuator Health Check**: `GET http://localhost:8080/actuator/health`
- **Actuator Routes Mapping**: `GET http://localhost:8080/actuator/gateway/routes`
- **Gateway Quick Health Check**: `GET http://localhost:8080/fallback/health`
