# Image de base "ubuntu-java" demandee au TP : Ubuntu + Java 21 (JRE).
# Publiee sur Docker Hub sous <DOCKERHUB_USERNAME>/ubuntu-java
FROM ubuntu:24.04

RUN apt-get update \
 && apt-get install -y --no-install-recommends openjdk-21-jre-headless ca-certificates curl \
 && rm -rf /var/lib/apt/lists/*

CMD ["java", "-version"]
