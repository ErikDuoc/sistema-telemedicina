# Multi-stage Dockerfile para el microservicio appointment-service
# - Build con Maven (JDK 21) y cache de dependencias
# - Imagen final mínima con Eclipse Temurin JRE 21

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copiar pom para cachear dependencias
COPY pom.xml ./

# Descargar dependencias en una capa separada
RUN mvn -B -DskipTests dependency:go-offline || true

# Copiar el código fuente y compilar
COPY src ./src
RUN mvn -B -DskipTests package


FROM eclipse-temurin:21-jre-jammy
LABEL maintainer="equipo-de-desarrollo"

# Crear usuario no-root
RUN groupadd -r spring && useradd -r -g spring spring

WORKDIR /app

# Copiar el jar compilado (usa comodín para el nombre del jar generado)
COPY --from=build /workspace/target/*.jar /app/app.jar

EXPOSE 8087

# Valores por defecto; pueden ser sobrescritos por docker-compose o docker run
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENV SPRING_PROFILES_ACTIVE=production

USER spring

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -jar /app/app.jar"]
