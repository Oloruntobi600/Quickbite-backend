# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /usr/src/Quickbite-project

COPY pom.xml . 
COPY src ./src

# Build the project and create the jar file
RUN mvn clean package -DskipTests

# Stage 2: Create the final image
FROM openjdk:17-jdk-slim

WORKDIR /Quickbite-project

# Update the JAR file name in the COPY command
COPY --from=build /usr/src/Quickbite-project/target/Online-Food-Ordering-0.0.1-SNAPSHOT.jar ./QuickbiteProject.jar

EXPOSE 5050

# Fix the typo in 'Quickbite-preoject.jar' to 'QuickbiteProject.jar'
ENTRYPOINT ["java", "-jar", "QuickbiteProject.jar"]
