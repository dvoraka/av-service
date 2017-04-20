FROM debian:stretch
MAINTAINER dvoraka <alen.dvorak@gmail.com>

RUN apt-get update \
    && apt-get --no-install-recommends -y install \
        clamav-daemon \
    && rm -rf /var/lib/apt/lists/*

COPY conf/clamd.conf /etc/clamav/

RUN freshclam

EXPOSE 3310

CMD ["clamd"]
