FROM openjdk:17-alpine
RUN mkdir -p /app

# Local build
# COPY ./build/libs/*.jar /app/app.jar

# Github build
COPY app.jar /app/app.jar

WORKDIR /app
ENTRYPOINT ["java","-jar","app.jar"]
