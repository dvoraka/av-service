#
# Docker configuration for a testing replication node.
#
# Latest snapshot version.
#

FROM debian:stretch
LABEL maintainer="dvoraka <alen.dvorak@gmail.com>"

ENV USER_HOME /home/user
ENV PROJECT_ROOT av-service

RUN apt-get update \
    && apt-get --no-install-recommends -y install \
        openjdk-8-jdk-headless \
        git-core \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN useradd -m -d "$USER_HOME" -s /bin/bash user

USER user
WORKDIR $USER_HOME

RUN git clone https://github.com/dvoraka/av-service.git

WORKDIR ${USER_HOME}/${PROJECT_ROOT}

RUN ./gradlew compileJava

COPY conf/avservice.properties ${USER_HOME}/${PROJECT_ROOT}/common/src/main/resources/
COPY conf/logback.xml ${USER_HOME}/${PROJECT_ROOT}/common/src/main/resources/
COPY conf/gradle.properties ${USER_HOME}/${PROJECT_ROOT}/

CMD ["./gradlew", "-q", "runAmqpReplicationService"]
