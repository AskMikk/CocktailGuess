FROM gradle:8.2.1-jdk17 AS builder
WORKDIR /app

COPY build.gradle ./
COPY src src

RUN gradle build -x test --no-daemon --refresh-dependencies
RUN ls -la /app/build/libs

FROM eclipse-temurin:17.0.12_7-jdk
WORKDIR /app

COPY --from=builder /app/build/libs/app-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]