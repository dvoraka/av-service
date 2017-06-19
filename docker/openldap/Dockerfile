FROM debian:stretch
MAINTAINER dvoraka <alen.dvorak@gmail.com>

RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive \
    apt-get --no-install-recommends -y install \
        slapd ldap-utils \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

#RUN dpkg-reconfigure -f noninteractive slapd

EXPOSE 389

CMD ["slapd", "-d", "32768", "-u", "openldap", "-g", "openldap"]
