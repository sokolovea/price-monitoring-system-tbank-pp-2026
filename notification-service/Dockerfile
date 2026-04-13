FROM gradle:9.4-jdk25 AS build

WORKDIR /app

COPY gradle gradle
COPY settings.gradle .
COPY .env .

COPY core core/

COPY notification-service/src ./notification-service/src
COPY notification-service/build.gradle.kts ./notification-service/

RUN gradle :notification-service:build -x test --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/notification-service/build/libs/*.jar app.jar
COPY --from=build /app/.env .

CMD ["java", "-jar", "app.jar"]