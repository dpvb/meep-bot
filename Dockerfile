FROM eclipse-temurin:17-jdk-jammy AS build
COPY ./pom.xml ./pom.xml
COPY ./src ./src
COPY .env ./.env
RUN mvn dependency:go-offline -B
RUN mvn clean package

FROM arm32v7/adoptopenjdk:17-jre-hotspot
WORKDIR /app
COPY --from=build /target/MeepBot.jar ./MeepBot.jar
COPY --from=build ./.env ./.env
CMD ["java", "-jar", "./MeepBot.jar"]
