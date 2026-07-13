FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
RUN mvn -B -DskipTests dependency:go-offline || true

COPY src ./src
RUN mvn -B -DskipTests package


FROM eclipse-temurin:21-jre-jammy
LABEL maintainer="equipo-de-desarrollo"

RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app

COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8761

ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=mysql

USER spring

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app/app.jar"]

