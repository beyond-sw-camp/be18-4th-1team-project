# Build Stage
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

COPY backend/smerp/gradlew ./
COPY backend/smerp/build.gradle backend/smerp/settings.gradle ./
COPY backend/smerp/gradle ./gradle
COPY backend/smerp/src ./src
#COPY backend/smerp/gradle.properties ./

RUN chmod +x ./gradlew

#RUN ./gradlew clean build -x test
RUN ./gradlew clean build -x test -PqueryDslVersion=7.0


ARG QUERYDSL_VERSION

ENV QUERYDSL_VERSION=${QUERYDSL_VERSION}

# Run Stage

FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# curl 설치 명령어 추가
RUN apk add --no-cache curl

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

