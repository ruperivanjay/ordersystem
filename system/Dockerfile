# Stage 1: Build the application using Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
# Copy the pom.xml and source code
COPY . .
# Build the jar
RUN mvn clean package -DskipTests

# Stage 2: Run the application using Java 17 JRE (smaller and more secure)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
