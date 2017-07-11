FROM solr:6.6.0
MAINTAINER dvoraka <alen.dvorak@gmail.com>

ENV SOLR_HOME=/opt/solr/server/solr/

ARG SOLR_CONF=${SOLR_HOME}/configsets
ARG SOLR_PORT=8983

COPY conf/ $SOLR_CONF/

USER root

RUN apt-get update \
    && apt-get --no-install-recommends -y install \
        procps \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

USER solr

RUN solr start -p $SOLR_PORT \
    && sleep 15 \
    && solr create -c test -d test -p $SOLR_PORT \
    && solr stop -all

EXPOSE $SOLR_PORT

CMD ["solr-foreground", "-m", "256m"]
