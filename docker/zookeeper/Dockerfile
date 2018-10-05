FROM debian:stretch
LABEL maintainer="dvoraka <alen.dvorak@gmail.com>"

ENV DEBIAN_FRONTEND noninteractive

RUN apt-get update \
    && apt-get --no-install-recommends -y install zookeeper \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY conf/zoo.cfg /etc/zookeeper/conf/
COPY conf/log4j.properties /etc/zookeeper/conf/

EXPOSE 2181

CMD ["/usr/share/zookeeper/bin/zkServer.sh", "start-foreground"]
