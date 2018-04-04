# Copyright 2018, DELL, Inc.
ARG repo=maven
ARG tag=3.5.3-jdk-8-alpine

FROM ${repo}:${tag}
COPY . /notify_example/
COPY settings.xml /root/.m2/settings.xml

WORKDIR /notify_example
RUN mvn clean install -DskipTests
ENTRYPOINT ["sh", "-c", "mvn exec:java"]
