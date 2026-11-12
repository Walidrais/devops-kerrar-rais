# Image de l'application, construite au-dessus de notre image ubuntu-java.
# Le workflow GitHub passe --build-arg BASE_IMAGE=<DOCKERHUB_USERNAME>/ubuntu-java:latest
ARG BASE_IMAGE=ubuntu-java:latest

# 1) Compilation avec Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src src
RUN mvn -q -B package -DskipTests

# 2) Execution sur ubuntu-java
FROM ${BASE_IMAGE}
WORKDIR /app
COPY --from=build /build/target/app.jar app.jar
# utilisateur non-root (fourni par l'image ubuntu:24.04)
USER ubuntu
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
