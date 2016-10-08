FROM solr:6.2.1

ARG SOLR_CONF=/opt/solr/server/solr/configsets
ARG SOLR_PORT=8983

COPY conf/ $SOLR_CONF/

RUN solr start -p $SOLR_PORT \
    && sleep 15 \
    && solr create -c test -d $SOLR_CONF/test -p $SOLR_PORT \
    && solr stop -all

EXPOSE $SOLR_PORT

CMD ["solr"]
