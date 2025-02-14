# Stage 1: Build the application
FROM gradle:7.6.0-jdk17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy only Gradle wrapper and configuration files first (to cache dependencies)
COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle

# Grant execute permissions to gradlew
RUN chmod +x /app/gradlew

# Pre-download dependencies to leverage Docker layer caching
RUN ./gradlew dependencies --no-daemon || true

# Copy the rest of the application files
COPY . .

# Grant execute permissions again in case permissions were reset during the COPY
RUN chmod +x /app/gradlew

# Build the application
RUN ./gradlew clean build --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:17-jre

# Set the runtime working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
