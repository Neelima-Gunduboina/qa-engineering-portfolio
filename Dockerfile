# Runs the API + DB suites (no browser needed) in a clean container.
#   docker build -t parabank-qa .
#   docker run --rm parabank-qa
FROM maven:3.9-eclipse-temurin-17
WORKDIR /workspace
COPY pom.xml .
RUN mvn -B -q dependency:go-offline
COPY src ./src
CMD ["mvn", "-B", "test", "-Papi-db"]
