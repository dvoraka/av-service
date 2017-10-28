FROM solr:6.6.2
MAINTAINER dvoraka <alen.dvorak@gmail.com>

ENV SOLR_HOME=/opt/solr/server/solr/

ARG SOLR_CONF=${SOLR_HOME}/configsets
ARG SOLR_PORT=8983

COPY conf/ $SOLR_CONF/

RUN solr start -p $SOLR_PORT \
    # wait for start
    && sleep 15 \
    # create messageinfo core
    && solr create -c messageinfo -d test -p $SOLR_PORT \
    # stop Solr
    && solr stop -all

EXPOSE $SOLR_PORT

CMD ["solr-foreground", "-m", "256m"]
