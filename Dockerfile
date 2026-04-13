FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY . .
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline
RUN ./mvnw package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/smart-onboarding.jar"]