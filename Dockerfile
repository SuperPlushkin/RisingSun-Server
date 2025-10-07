# Multi-stage build
FROM gradle:8.7-jdk21 AS builder
WORKDIR /app

COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src

RUN gradle clean bootJar --no-daemon

FROM openjdk:21-jdk-slim

# ✅ Добавляем мета-информацию для Docker Hub
LABEL maintainer="kirill.vldk@gmail.com"
LABEL version="1.0.0"
LABEL description="Spring Boot RisingSun Server For Messenger"

WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]