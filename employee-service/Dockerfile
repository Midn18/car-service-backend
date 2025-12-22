FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

USER root
RUN apk add --no-cache curl

COPY gradlew .
COPY gradlew.bat .
COPY gradle/wrapper gradle/wrapper/
COPY settings.gradle.kts build.gradle.kts ./

COPY common-api/src common-api/src/
COPY common-api/build.gradle.kts common-api/

COPY employee-service/src employee-service/src/
COPY employee-service/build.gradle.kts employee-service/

RUN chmod +x gradlew
RUN ./gradlew :employee-service:build -x test --no-daemon

# Runtime
FROM --platform=$TARGETPLATFORM eclipse-temurin:21-jre-alpine
WORKDIR /app

ARG APP_USER=appuser
ARG APP_UID=1000
RUN addgroup -g 1000 -S $APP_USER && \
    adduser -u $APP_UID -S $APP_USER -G $APP_USER

COPY --from=builder --chown=$APP_USER:$APP_USER /app/employee-service/build/libs/employee-service-*.jar app.jar

USER $APP_USER
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]