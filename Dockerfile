# Stage 1: Build the application
FROM gradle:7.6.0-jdk11 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# Stage 2: Run the application
FROM openjdk:17.0.1-jre-slim
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]