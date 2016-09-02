#!/bin/bash
#
# Prepares PostgreSQL
#

psql -c "CREATE USER docker WITH SUPERUSER PASSWORD 'docker';" -U postgres
createdb -O docker -E UTF8 -T template0 docker -U postgres


exit 0
