# multi-stage build: run this with context = project root (eteeapp\eteeapp) using:
# docker build -f target\Dockerfile -t yourname/eteeap:latest .
FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /app
# copy pom.xml from build context root
COPY pom.xml .
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml dependency:go-offline

# copy source from build context root
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
# copy the built jar from the build stage and give it a stable name
COPY --from=build /app/target/eteeapp-0.0.1-SNAPSHOT.jar /app/app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]