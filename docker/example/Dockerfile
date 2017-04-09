
# Example Docker configuration for service running.

FROM debian:stretch

ARG VERSION=0.6

ENV USER_HOME /home/user
ENV PROJECT_ROOT av-service

RUN apt-get update \
    && apt-get -y install \
        openjdk-8-jdk-headless \
        git-core \
    && rm -rf /var/lib/apt/lists/*

RUN useradd -m -d $USER_HOME -s /bin/bash user

USER user
WORKDIR $USER_HOME

RUN git clone https://github.com/dvoraka/av-service.git

WORKDIR ${USER_HOME}/${PROJECT_ROOT}

RUN git checkout v${VERSION} \
    && ./gradlew compileJava


CMD ["./gradlew", "-q", "runCustomServer"]
