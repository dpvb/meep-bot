FROM maven:3.9.9-amazoncorretto-17 AS build

COPY ./pom.xml ./pom.xml
RUN mvn dependency:go-offline -B

COPY ./src ./src
COPY .env ./.env
RUN mvn clean package

FROM amazoncorretto:17
WORKDIR /app
COPY --from=build /target/MeepBot.jar ./MeepBot.jar
COPY --from=build ./.env ./.env
CMD ["java", "-jar", "./MeepBot.jar"]