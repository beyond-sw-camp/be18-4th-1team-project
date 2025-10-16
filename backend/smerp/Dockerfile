# Build Stage

FROM eclipse-temurin:21-jdk-alpine AS builder
ARG QUERYDSLVERSION

WORKDIR /app

COPY backend/smerp/gradlew ./
COPY backend/smerp/build.gradle backend/smerp/settings.gradle ./
COPY backend/smerp/gradle ./gradle
COPY backend/smerp/src ./src
#COPY backend/smerp/gradle.properties ./

RUN chmod +x ./gradlew

#RUN ./gradlew clean build -x test
RUN ./gradlew clean build -x test -PqueryDslVersion=${QUERYDSLVERSION}


# Run Stage
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

RUN apk add --no-cache curl

RUN curl -LO https://dl.k8s.io/release/v1.26.0/bin/linux/amd64/kubectl && \
    chmod +x ./kubectl && \
    mv ./kubectl /usr/local/bin/kubectl

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

