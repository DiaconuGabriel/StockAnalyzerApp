# Stage 1: Build the application
FROM gradle:7.6.0-jdk17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy Gradle wrapper and configuration files first (for build caching)
COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle

# Ensure the Gradle wrapper is executable
RUN chmod +x /app/gradlew

# Pre-download dependencies (this helps cache dependencies)
RUN ./gradlew dependencies --no-daemon || true

# Copy the rest of the project files into the container
COPY . .

# Build the application (clean build without needing to skip tests)
RUN ./gradlew clean build --no-daemon

# Stage 2: Run the application
FROM eclipse-temurin:17-jre

# Set the runtime working directory
WORKDIR /app

# Copy the built JAR file from the first stage to the runtime image
COPY --from=build /app/build/libs/*.jar app.jar

# Expose the application port
EXPOSE 8081

# Define the command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
