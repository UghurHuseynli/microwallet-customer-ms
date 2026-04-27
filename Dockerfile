# ── Stage 1: Build ────────────────────────────────────────────
FROM gradle:8.14-jdk21 AS builder
WORKDIR /app

COPY . .

RUN gradle bootJar --no-daemon -x test

# ── Stage 2: Run ──────────────────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]