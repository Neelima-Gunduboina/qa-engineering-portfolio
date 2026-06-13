FROM maven:3.9.5-eclipse-temurin-17
WORKDIR /workspace
COPY . /workspace
RUN mvn -q -DskipTests=false -Psmoke test || true
