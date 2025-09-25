#
# Unified Dockerfile for all PingTower services (optimized)
#
# Features:
# - Multi-stage сборка (build + runtime)
# - Поддержка всех сервисов PingTower
# - Пропуск тестов при сборке (по умолчанию)
# - Кэширование зависимостей Gradle
#
# Требования:
# - Docker 20.10+
#
# Использование:
# docker build --build-arg SERVICE_NAME=notificator -t pingtower-notificator .
#
# Переменные сборки:
# SERVICE_NAME - обязательный, имя сервиса для сборки (например notificator)
# SKIP_TESTS - пропускать тесты (по умолчанию true)
#
ARG BUILD_HOME=/build

#
# Gradle image for the build stage.
#
FROM eclipse-temurin:25-jdk-alpine AS build-image

#
# Set the working directory.
#
ARG SERVICE_NAME
ARG BUILD_HOME
ENV APP_HOME=$BUILD_HOME
WORKDIR $APP_HOME

#
# Copy only build files first to cache dependencies
#
COPY gradle $APP_HOME/gradle/
COPY gradlew $APP_HOME/
RUN ./gradlew --no-daemon --version
COPY settings.gradle build.gradle $APP_HOME/

# Download dependencies first (cached unless build.gradle changes)
RUN ./gradlew dependencies --no-daemon

# Copy source code after dependencies are cached
COPY common/ $APP_HOME/common/
COPY ${SERVICE_NAME}/ $APP_HOME/${SERVICE_NAME}/

#
# Build the specified service
#
RUN ./gradlew :${SERVICE_NAME}:build --no-daemon -x test;

#
# Java image for the application to run in.
#
FROM eclipse-temurin:25-jre-alpine

#
# Build arguments
#
ARG BUILD_HOME
ARG SERVICE_NAME
ENV APP_HOME=$BUILD_HOME

#
# Install curl for healthchecks
#
RUN apk add --no-cache curl

#
# Copy the jar file and name it app.jar
#
COPY --from=build-image $APP_HOME/${SERVICE_NAME}/build/libs/${SERVICE_NAME}-0.0.1-SNAPSHOT.jar app.jar

#
# The command to run when the container starts.
#
CMD ["java", "-jar", "app.jar"]