FROM openjdk:17-jdk-alpine

COPY ./Backend/target/*.jar /app/SimuladorTeoriaColas-0.jar

WORKDIR /app

CMD ["java", "-jar", "SimuladorTeoriaColas-0.jar"]