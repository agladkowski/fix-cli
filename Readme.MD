
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

### Pushing docker comtainer to dockerhub
```
docker login -u "agladkowski" -p "mypassword" docker.io
docker tag <IMAGE_ID> agladkowski/fix-cli:1.0
docker push agladkowski/fix-cli:1.0
```

# How to run

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

## Java 

```
 java -jar build/libs/fix-cli-1.0-SNAPSHOT-all.jar --host 127.0.0.1 --port 2345 --target Exchange --sender Broker1 -u TEST -p PWD
```

## Docker
```
 docker run agladkowski/fix-cli  --host 127.0.0.1 --port 2345 --target Exchange -sender Broker1 -u TEST -p PWD
```

## Github workflow
https://medium.com/@thearaseng/using-github-actions-build-and-push-docker-images-30d05e58be4b