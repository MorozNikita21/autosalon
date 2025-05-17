FROM gradle:8.10.2-jdk21 AS build

WORKDIR /workspace
COPY . .
RUN gradle clean bootJar --no-daemon

FROM gcr.io/distroless/java17-debian11

COPY --from=build /workspace/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]