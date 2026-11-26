# Multi-stage Dockerfile for Registry Service using Java 21

FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

COPY pom.xml ../pom.xml
COPY shared/event-contracts ../shared/event-contracts
COPY registry-service/pom.xml .

RUN mvn dependency:go-offline -B -f ../pom.xml -pl :registry-service -am || true

COPY registry-service/src ./src
RUN mvn clean package -DskipTests -f ../pom.xml -pl :registry-service -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY --from=builder /build/registry-service/target/*.jar app.jar

ENV SERVER_PORT=8085
ENV JAVA_OPTS="-XX:+UseZGC -XX:+ZGenerational -XX:MaxRAMPercentage=75.0"

EXPOSE 8085

HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
