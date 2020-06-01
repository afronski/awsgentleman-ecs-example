FROM openjdk:11.0.7-jre-slim

LABEL maintainer="wojciech.gawronski@pattern-match.com"

RUN apt-get update -qq

VOLUME /tmp

ARG JAR_FILE
COPY ${JAR_FILE} app.jar

ENTRYPOINT [ "java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/app.jar" ]
