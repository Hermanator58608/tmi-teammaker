#
# Build stage
#
FROM maven:3.9.1-amazoncorretto-19-debian AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -f /home/app/pom.xml clean package

#
# Package stage
#
FROM openjdk:19
COPY --from=build /home/app/target/tmi_teammaker-0.0.1-SNAPSHOT.jar /usr/local/lib/application.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/application.jar"]