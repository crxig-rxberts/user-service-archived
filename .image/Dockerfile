# Use an official Maven image with JDK 17 as the base image
FROM maven:3.8.4-openjdk-17 as builder

# Set the working directory
WORKDIR /app

# Copy your project's pom.xml file
COPY ../pom.xml .

# Download dependencies
RUN mvn -B dependency:go-offline

# Copy the rest of your project's source code
COPY ../src ./src

# Build the project
RUN mvn -B clean package

# Use an official OpenJDK 17 image as the base image for the final stage
FROM openjdk:17-slim

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the builder stage
COPY --from=builder /app/target/*.jar ./app.jar

# Expose the port your application runs on
EXPOSE 8080

# Run your application
CMD ["java", "-jar", "app.jar"]