FROM java:8

ENV AMQ_NAME activemq
ENV AMQ_VERSION 5.13.3
ENV ARCHIVE_NAME apache-$AMQ_NAME-$AMQ_VERSION-bin.tar.gz
ENV DOWN_URL http://archive.apache.org/dist/$AMQ_NAME/$AMQ_VERSION/$ARCHIVE_NAME
ENV AMQ_DIR /opt/apache-$AMQ_NAME-$AMQ_VERSION

ENV ACTIVEMQ_TCP 61616
#ENV ACTIVEMQ_AMQP=5672
#ENV ACTIVEMQ_STOMP=61613
#ENV ACTIVEMQ_MQTT=1883
#ENV ACTIVEMQ_WS=61614
ENV ACTIVEMQ_HTTP=8161

ENV ACTIVEMQ_HOME /opt/activemq

RUN curl -O $DOWN_URL \
    && mkdir -p /opt \
    && tar xzf $ARCHIVE_NAME -C /opt/ \
    && rm $ARCHIVE_NAME \
    && ln -s $AMQ_DIR $ACTIVEMQ_HOME \
    && useradd -r -M -d $ACTIVEMQ_HOME activemq \
    && chown -R activemq:activemq $AMQ_DIR

USER activemq

COPY conf/activemq.xml $ACTIVEMQ_HOME/conf/

WORKDIR $ACTIVEMQ_HOME

EXPOSE $ACTIVEMQ_TCP
EXPOSE $ACTIVEMQ_HTTP

CMD ["/bin/bash", "-c", "bin/activemq console"]