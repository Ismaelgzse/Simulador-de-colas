# Build the angular project (the application frontend)
FROM node:18.16.0 AS angular
COPY frontend /app/frontend
WORKDIR /app/frontend
RUN npm install
RUN npm run build

# Build the spring boot application with maven and copy the generated files of the angular frontend to the static folder
FROM maven:3.8.4-openjdk-17-slim AS maven
COPY backend /app/backend
COPY --from=angular /app/frontend/dist/frontend /app/backend/src/main/resources/static/app
WORKDIR /app/backend
RUN mvn -f /app/backend/pom.xml clean package

# Create the docker image
FROM openjdk:17-oracle
WORKDIR /app/backend
COPY --from=maven /app/backend/target/SimuladorTeoriaColas-0.jar SimuladorTeoriaColas-0.jar
EXPOSE 8443
CMD ["java", "-jar", "SimuladorTeoriaColas-0.jar"]