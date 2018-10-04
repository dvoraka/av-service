FROM debian:stretch
LABEL maintainer="dvoraka <alen.dvorak@gmail.com>"

RUN apt-get update \
    && apt-get --no-install-recommends -y install \
        rabbitmq-server \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY conf/rabbitmq-env.conf /etc/rabbitmq/
COPY conf/rabbitmq.config /etc/rabbitmq/

COPY bin/prepareRMQ.sh /root
RUN rabbitmq-server -detached \
    && sleep 10 \
    && cat /var/log/rabbitmq/rabbitmq@localhost.log \
    && rabbitmq-plugins enable rabbitmq_management \
    && /root/prepareRMQ.sh \
    && rabbitmqctl status \
    && rabbitmqctl stop

EXPOSE 5672
EXPOSE 15672

CMD ["rabbitmq-server"]
