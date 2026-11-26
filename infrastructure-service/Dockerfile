# Multi-stage Dockerfile for Infrastructure Service using Java 21

FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY pom.xml ../pom.xml
COPY shared/event-contracts ../shared/event-contracts
COPY infrastructure-service/pom.xml .

RUN mvn dependency:go-offline -B -f ../pom.xml -pl :infrastructure-service -am || true

COPY infrastructure-service/src ./src
RUN mvn clean package -DskipTests -f ../pom.xml -pl :infrastructure-service -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /build/infrastructure-service/target/*.jar app.jar

ENV SERVER_PORT=8086
ENV JAVA_OPTS="-XX:+UseZGC -XX:+ZGenerational -XX:MaxRAMPercentage=75.0"

EXPOSE 8086

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8086/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
