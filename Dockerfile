FROM maven:3.8.4-openjdk-17 AS build
COPY ./pom.xml ./pom.xml
COPY ./src ./src
COPY .env ./.env
RUN mvn dependency:go-offline -B
RUN mvn clean package

FROM adoptopenjdk:17-jre-hotspot-buster-slim
WORKDIR /app
COPY --from=build /target/MeepBot.jar ./MeepBot.jar
COPY --from=build ./.env ./.env
CMD ["java", "-jar", "./MeepBot.jar"]
