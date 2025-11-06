# ------------------
# 1. BUILD STAGE
# ------------------
# FIX: Using the official '3.9-eclipse-temurin-21' tag, which is guaranteed to exist
# (Eclipse Temurin is a popular, open-source JDK distribution)
FROM maven:3.9-eclipse-temurin-21 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml file first to optimize caching (downloads dependencies)
COPY pom.xml .

# Copy the source code
COPY src ./src

# Build the JAR file
RUN mvn clean package -DskipTests
# ------------------
# 2. RUNNING STAGE
# ------------------
# This part is correct and should now work
FROM eclipse-temurin:21-jdk AS runtime
# Set the working directory
WORKDIR /app

# Copy the resulting JAR file from the build stage
COPY --from=build /app/target/VolleyBallStatTracker-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port (8080 is default for Spring Boot)
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
