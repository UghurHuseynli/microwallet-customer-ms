# ── Stage 1: Build payment-service ────────────────────────────
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /app

COPY . .
RUN chmod +x gradlew && ./gradlew bootJar --no-daemon -x test

# ── Stage 2: Run ──────────────────────────────────────────────
FROM eclipse-temurin:25-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
