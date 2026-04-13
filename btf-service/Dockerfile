FROM gradle:9.4-jdk25 AS build

ENV SERVICE_NAME=btf-service

WORKDIR /app

COPY gradle gradle
COPY settings-docker.gradle settings.gradle
COPY .env .

COPY core core/
COPY btf-service/src ./btf-service/src
COPY btf-service/build.gradle.kts ./btf-service/

RUN gradle :btf-service:build -x test --no-daemon

FROM eclipse-temurin:25-jre

WORKDIR /app

COPY --from=build /app/btf-service/build/libs/*.jar app.jar
COPY --from=build /app/.env .

CMD ["java", "-jar", "app.jar"]