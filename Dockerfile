# 1단계: 빌드
FROM gradle:8.5.0-jdk17 AS build
WORKDIR /home/app
COPY --chown=gradle:gradle . .
RUN ./gradlew build -x test

# 2단계: 실행
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /home/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
