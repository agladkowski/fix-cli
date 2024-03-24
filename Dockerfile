FROM openjdk:17-alpine
RUN mkdir -p /app

# Local build
# COPY ./build/libs/*.jar /app/app.jar

# Github build
COPY app.jar /app/

WORKDIR /app
ENTRYPOINT ["java","-jar","app.jar"]
