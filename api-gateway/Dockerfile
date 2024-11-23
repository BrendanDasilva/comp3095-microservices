#---------------
# BUILD STAGE
#---------------

FROM  gradle:8-jdk22-alpine AS builder

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build -x test

#---------------
# PACKAGE STAGE
#---------------

FROM openjdk:22-jdk-slim

WORKDIR mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar api-gateway.jar

EXPOSE 9000

ENTRYPOINT ["java", "-jar", "api-gateway.jar"]