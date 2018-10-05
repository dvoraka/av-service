FROM debian:stretch
LABEL maintainer="dvoraka <alen.dvorak@gmail.com>"

ENV DEBIAN_FRONTEND noninteractive
ENV SCALA_VERSION 2.12
ENV KAFKA_VERSION 1.0.0
ENV KAFKA_HOME /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"

RUN apt-get update \
    && apt-get --no-install-recommends -y install \
        openjdk-8-jre-headless \
        wget \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN wget -q http://apache.mirrors.spacedump.net/kafka/"$KAFKA_VERSION"/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz \
        -O /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz \
    && tar xfz /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -C /opt \
    && rm /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz

COPY scripts/start-kafka.sh /usr/bin/start-kafka.sh

COPY conf/server.properties $KAFKA_HOME/config/
COPY conf/log4j.properties $KAFKA_HOME/config/

EXPOSE 9099

CMD ["start-kafka.sh"]
