FROM eclipse-temurin:17-jdk-jammy AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw mvnw.cmd pom.xml ./
RUN chmod +x mvnw && ./mvnw -B -ntp dependency:go-offline

COPY src src
RUN ./mvnw -B -ntp clean package -DskipTests

FROM eclipse-temurin:17-jre-jammy

RUN groupadd --system spring && useradd --system --gid spring spring
USER spring:spring
WORKDIR /app

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70.0 -XX:InitialRAMPercentage=20.0 -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError"

COPY --from=build --chown=spring:spring /workspace/target/novamarket.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
