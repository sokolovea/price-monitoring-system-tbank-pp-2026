FROM gradle:9.4-jdk25 AS build

ENV SERVICE_NAME=notification-service

WORKDIR /app

COPY gradle gradle
COPY settings-docker.gradle settings.gradle
COPY .env .

COPY core core/
COPY notification-service notification-service/
COPY notification-service/build.gradle.kts ./notification-service/

RUN gradle :notification-service:build -x test --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/notification-service/build/libs/*.jar app.jar
COPY --from=build /app/.env .

CMD ["java", "-jar", "app.jar"]