FROM maven:3.9-eclipse-temurin-21

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B clean verify

EXPOSE 8081

CMD ["java", "-jar", "target/maven-hello-1.0.0.jar"]