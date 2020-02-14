FROM debian:stretch
LABEL maintainer="dvoraka <alen.dvorak@gmail.com>"

RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive \
    apt-get --no-install-recommends -y install \
        clamav-daemon \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY conf/clamd.conf /etc/clamav/

#RUN freshclam

EXPOSE 3310

CMD ["clamd"]
