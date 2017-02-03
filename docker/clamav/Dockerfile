FROM debian:stretch

RUN apt-get update \
    && apt-get -y install \
        clamav-daemon \
    && rm -rf /var/lib/apt/lists/*

COPY conf/clamd.conf /etc/clamav/

RUN freshclam

EXPOSE 3310

CMD ["clamd"]
