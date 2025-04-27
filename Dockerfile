FROM gradle:8.13-jdk-21-and-23-alpine AS builder

WORKDIR /usr/app

COPY build.gradle.kts settings.gradle.kts gradle.properties gradlew /usr/app/
COPY gradle /usr/app/gradle

RUN ./gradlew build --no-daemon || return 0

COPY src /usr/app/src

RUN ./gradlew clean bootJar --no-daemon

FROM openjdk:21-slim

ENV APP_HOME=/app
WORKDIR $APP_HOME

COPY --from=builder /usr/app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
