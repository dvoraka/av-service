FROM debian:jessie

RUN apt-get update \
    && apt-get -y install \
        rabbitmq-server \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 15672

CMD ["rabbitmq-server"]
