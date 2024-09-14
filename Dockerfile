FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /usr/src/Quickbite-projects

#COPY settings.xml /usr/share/maven/conf/settings.xml

COPY pom.xml .

COPY src ./src

RUN mvn -B clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /Quickbite-project

COPY --from=build /usr/src/Quickbite-project/target/Quickbite-project-0.0.1-SNAPSHOT.jar Quickbite-project.jar

ENTRYPOINT ["java","-jar","Quickbite-preoject.jar"]