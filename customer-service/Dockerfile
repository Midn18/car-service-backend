FROM --platform=$BUILDPLATFORM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

COPY gradlew .
COPY gradlew.bat .
COPY gradle/wrapper gradle/wrapper/
COPY settings.gradle.kts build.gradle.kts ./

COPY common-api/src common-api/src/
COPY common-api/build.gradle.kts common-api/

COPY customer-service/src customer-service/src/
COPY customer-service/build.gradle.kts customer-service/

RUN chmod +x gradlew
RUN ./gradlew :customer-service:build -x test --no-daemon

FROM --platform=$TARGETPLATFORM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/customer-service/build/libs/customer-service-*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]