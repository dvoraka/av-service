FROM debian:jessie

RUN apt-get -y update

RUN apt-get -y install clamav-daemon
ADD conf/clamd.conf /etc/clamav/

RUN freshclam

EXPOSE 3310

CMD ["/etc/init.d/clamav-daemon", "start"]
