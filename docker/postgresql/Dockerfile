FROM debian:stretch
MAINTAINER dvoraka <alen.dvorak@gmail.com>

ENV PG_VERSION=9.6
ENV PG_DATA=/var/lib/postgresql/$PG_VERSION/main

RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive \
    apt-get --no-install-recommends -y install \
        postgresql \
        postgresql-contrib \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

COPY conf/pg_hba.conf /etc/postgresql/$PG_VERSION/main/

RUN mkdir -p /var/run/postgresql/"$PG_VERSION"-main.pg_stat_tmp
RUN chown -R postgres:postgres /var/run/postgresql/"$PG_VERSION"-main.pg_stat_tmp
RUN chown postgres:postgres /etc/postgresql/"$PG_VERSION"/main/pg_hba.conf

USER postgres

RUN /etc/init.d/postgresql start \
    && psql --command "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" \
    && createdb -O docker -E UTF8 -T template0 docker

EXPOSE 5432

CMD /usr/lib/postgresql/${PG_VERSION}/bin/postgres \
    -c config_file=/etc/postgresql/${PG_VERSION}/main/postgresql.conf
