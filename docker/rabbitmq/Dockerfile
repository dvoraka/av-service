FROM debian:jessie

RUN apt-get -y update

RUN apt-get -y install rabbitmq-server

EXPOSE 15672

CMD ["rabbitmq-server"]
