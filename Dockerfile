FROM eclipse-temurin:17-jdk-focal

WORKDIR /app

RUN mkdir json
COPY .mvn/ ./mvn
COPY pom.xml mvnw ./
COPY ./.mvn/wrapper/maven-wrapper.properties .mvn/wrapper/maven-wrapper.properties
RUN sed -i 's/\r$//' mvnw
RUN ./mvnw dependency:go-offline

COPY src ./src

CMD ["./mvnw", "spring-boot:run"]
