FROM gradle:9.4-jdk25 AS build

WORKDIR /app

COPY gradle gradle
COPY settings.gradle .
COPY .env .

COPY integration-service/src ./integration-service/src
COPY integration-service/build.gradle.kts ./integration-service/

RUN gradle :integration-service:build -x test --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/integration-service/build/libs/*.jar app.jar
COPY --from=build /app/.env .

CMD ["java", "-jar", "app.jar"]