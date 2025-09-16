FROM gradle:8.12-jdk21 AS build

WORKDIR /app

# Copiar Gradle y propiedades
COPY gradle/ gradle/
COPY gradlew gradlew
COPY gradlew.bat gradlew.bat
COPY gradle/wrapper/ gradle/wrapper/
COPY build.gradle settings.gradle main.gradle gradle.properties ./

# Copiar código fuente
COPY applications/ ./applications/
COPY domain/ ./domain/
COPY infrastructure/ ./infrastructure/

# Build app-service
RUN chmod +x ./gradlew && ./gradlew :app-service:build --no-daemon -x test -x validateStructure

# Runtime
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/applications/app-service/build/libs/*.jar app-service.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app-service.jar"]