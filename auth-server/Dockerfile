# auth-server/Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and project files
COPY auth-server/mvnw .
COPY auth-server/.mvn .mvn
COPY auth-server/pom.xml .

# Include starter so it can be installed locally
COPY starter-banco/pom.xml starter-banco/pom.xml
COPY starter-banco/src starter-banco/src

# Download dependencies
RUN ./mvnw -f starter-banco/pom.xml -DskipTests install
RUN ./mvnw dependency:go-offline -B

# Copy source
COPY auth-server/src src

# Build
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

ARG DEPENDENCY=/app/target/dependency

# Copy dependencies
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Expose port
EXPOSE 9000

# Run
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.banco.auth.AuthServerApplication"]
