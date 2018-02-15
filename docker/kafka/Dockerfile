FROM debian:stretch

MAINTAINER dvoraka <alen.dvorak@gmail.com>

ENV DEBIAN_FRONTEND noninteractive
ENV SCALA_VERSION 2.12
ENV KAFKA_VERSION 1.0.0
ENV KAFKA_HOME /opt/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION"

RUN apt-get update \
    && apt-get -y install \
        zookeeper \
        wget \
        supervisor \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN wget -q http://apache.mirrors.spacedump.net/kafka/"$KAFKA_VERSION"/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz \
        -O /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz \
    && tar xfz /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz -C /opt \
    && rm /tmp/kafka_"$SCALA_VERSION"-"$KAFKA_VERSION".tgz

#TODO: probably replace
ADD scripts/start-kafka.sh /usr/bin/start-kafka.sh
# Supervisor config
ADD supervisor/kafka.conf supervisor/zookeeper.conf /etc/supervisor/conf.d/

COPY conf/server.properties $KAFKA_HOME/config/
COPY conf/log4j.properties $KAFKA_HOME/config/

# 2181: ZooKeeper
# 9099: Kafka
EXPOSE 2181 9099

CMD ["supervisord", "-n"]
