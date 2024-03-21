FROM openjdk:17-alpine
RUN mkdir -p /app
COPY ./build/libs/*.jar /app/fix-cli.jar
WORKDIR /app
ENTRYPOINT ["java","-jar","fix-cli.jar"]