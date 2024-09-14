FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /usr/Quickbite-project

#COPY settings.xml /usr/share/maven/conf/settings.xml

COPY pom.xml .

COPY src ./src

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /Quickbite-project

COPY --from=build /usr/Quickbite-project/target/QuickbiteProject-0.0.1-SNAPSHOT.jar QuickbiteProject.jar

EXPOSE 5050

ENTRYPOINT ["java","-jar","Quickbite-preoject.jar"]