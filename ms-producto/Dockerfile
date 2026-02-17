# ms-producto/Dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Maven wrapper and project files
COPY ms-producto/mvnw .
COPY ms-producto/.mvn .mvn
COPY ms-producto/pom.xml .

# Include starter so it can be installed locally
COPY starter-banco/pom.xml starter-banco/pom.xml
COPY starter-banco/src starter-banco/src

RUN ./mvnw -f starter-banco/pom.xml -DskipTests install
RUN ./mvnw dependency:go-offline -B

COPY ms-producto/src src
RUN ./mvnw package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jre-alpine
ARG DEPENDENCY=/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
EXPOSE 8084
ENTRYPOINT ["java", "-cp", "app:app/lib/*", "com.banco.producto.MsProductoApplication"]
