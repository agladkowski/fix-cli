
<!-- TOC -->
* [Prerequisites](#prerequisites)
* [Building binaries](#building-binaries)
  * [Jar](#jar)
  * [Docker container](#docker-container)
* [How to run](#how-to-run)
  * [Java](#java-)
  * [Docker](#docker)
  * [Program options](#program-options)
* [Pushing docker container to dockerhub](#pushing-docker-container-to-dockerhub)
  * [Github workflow setup](#github-workflow-setup)
<!-- TOC -->

# Prerequisites
- Java17

# Building binaries
## Jar
```
./gradlew shadowJar
```

## Docker container
```
docker build --no-cache . -t agladkowski/fix-cli
```

# How to run

## Java 

```
java -jar build/libs/fix-cli-1.0-SNAPSHOT-all.jar \
 --host 127.0.0.1 \
 --port 2345 \
 --target Exchange \
 --sender Broker1 \
 -u TEST \
 -p PWD
```

## Docker
```
docker run agladkowski/fix-cli \
    --host 127.0.0.1 \
    --port 2345 \
    --target Exchange \
    --sender Broker1 \
    -u TEST \
    -p PWD
```

## Program options
```
Usage: fix-cli options_list
Options: 
    --host -> Host (always required) { String }
    --port -> Port (always required) { Int }
    --sender, -s -> SenderCompId (always required) { String }
    --target, -t -> TargetCompId (always required) { String }
    --username, -u -> Username { String }
    --password, -p -> Password { String }
    --fixVer -> Fix protocol version, default 4.4 { String }
    --verbose -> Enable verbose logging mode 
    --help, -h -> Usage info 
```

# Pushing docker container to dockerhub
```
docker login -u agladkowski
docker tag <IMAGE_ID> agladkowski/fix-cli:1.0
docker push agladkowski/fix-cli:1.0
```

## Github workflow setup
https://medium.com/@thearaseng/using-github-actions-build-and-push-docker-images-30d05e58be4b