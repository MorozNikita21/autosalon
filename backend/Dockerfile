FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY build.gradle settings.gradle gradle.properties gradlew ./
COPY gradle gradle
COPY config config

RUN chmod +x gradlew && ./gradlew --no-daemon dependencies

COPY src src
RUN ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app

RUN mkdir -p logs \
    && chown -R 1000:1000 logs \
    && chmod 755 logs

COPY --from=builder /app/build/libs/*.jar app.jar

USER 1000
ENTRYPOINT ["java","-jar","app.jar"]