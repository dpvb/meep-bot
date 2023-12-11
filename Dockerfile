FROM arm32v7/maven:3.9.5-eclipse-temurin-11 AS build
COPY ./pom.xml ./pom.xml
COPY ./src ./src
COPY .env ./.env
RUN mvn dependency:go-offline -B
RUN mvn clean package

FROM arm32v7/adoptopenjdk:11-jre-hotspot-bionic
WORKDIR /app
COPY --from=build /target/MeepBot.jar ./MeepBot.jar
COPY --from=build ./.env ./.env
CMD ["java", "-jar", "./MeepBot.jar"]