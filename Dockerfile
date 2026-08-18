FROM maven:3.9.11-eclipse-temurin-25 AS build

WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime
FROM eclipse-temurin:25-jre-alpine

# Install curl and CA certs
RUN apk --no-cache add curl wget netcat-openbsd ca-certificates

RUN mkdir -p /app/certs && \
    curl -o /app/certs/rds-combined-ca-bundle.pem https://truststore.pki.rds.amazonaws.com/global/global-bundle.pem

# ARG PROFILE=dev
ARG APP_VERSION=0.0.1

WORKDIR /app
COPY --from=build /build/target/rag-*.jar /app/app.jar

EXPOSE 8080

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "app.jar"]