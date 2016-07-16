FROM debian:jessie

ENV VERSION=9.4
ENV PGDATA=/var/lib/postgresql/$VERSION/main

RUN apt-get update \
    && apt-get -y install \
        postgresql \
    && rm -rf /var/lib/apt/lists/*

COPY conf/pg_hba.conf /etc/postgresql/$VERSION/main/

RUN chown postgres:postgres /etc/postgresql/$VERSION/main/pg_hba.conf

EXPOSE 5432

USER postgres

CMD /usr/lib/postgresql/${VERSION}/bin/postgres -c config_file=/etc/postgresql/$VERSION/main/postgresql.conf
