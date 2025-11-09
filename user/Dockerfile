# --- build stage ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app

# Copy wrapper and pom first for better caching
COPY .mvn .mvn
COPY mvnw mvnw
COPY pom.xml .

# Ensure wrapper uses Unix line endings and is executable
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

# Pre-fetch dependencies
RUN ./mvnw -B -q -DskipTests dependency:go-offline

# Copy the source code
COPY src src

# Build the application
RUN ./mvnw -B -q -DskipTests package

# --- runtime stage ---
FROM eclipse-temurin:25-jre
WORKDIR /app

RUN apt-get update && apt-get install -y --no-install-recommends wget \
    && rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
HEALTHCHECK --interval=30s --timeout=3s --start-period=20s --retries=5 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"' || exit 1
ENTRYPOINT ["java","-jar","/app/app.jar"]
