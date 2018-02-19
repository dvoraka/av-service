#!/bin/sh

# Run PostgreSQL
exec /usr/lib/postgresql/${PG_VERSION}/bin/postgres \
        -c config_file=/etc/postgresql/${PG_VERSION}/main/postgresql.conf
