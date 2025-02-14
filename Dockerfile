# Stage 1: Build the application
FROM gradle:7.6.0-jdk17 AS build

# Create a working directory in the container
WORKDIR /app

# Copy Gradle wrapper and dependencies configuration first (for dependency caching)
COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle

# Pre-download dependencies to improve build caching (optional but recommended)
RUN ./gradlew dependencies --no-daemon || true

# Copy the rest of the application files (source code, etc.)
COPY . .

# Run the Gradle build command (skipping tests during the build)
RUN ./gradlew clean build -x test --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:17-jre

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the specified port
EXPOSE 8081

# Define the entry point to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
