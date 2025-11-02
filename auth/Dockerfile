FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml mvnw mvnw.cmd .
COPY .mvn .mvn
COPY src src
RUN ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/demo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
