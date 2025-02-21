FROM gradle:8.12.1-jdk17 AS builder
WORKDIR /app
COPY gradlew ./
COPY gradle/wrapper ./gradle/wrapper
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
COPY src ./src
VOLUME /home/gradle/.gradle
RUN ./gradlew clean build --no-daemon
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/user-profile.jar user-profile.jar
EXPOSE 8090
CMD ["java", "-jar", "user-profile.jar"]
