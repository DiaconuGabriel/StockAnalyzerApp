# Stage 1: Build the application
FROM gradle:7.6.0-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# Stage 2: Run the application
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]