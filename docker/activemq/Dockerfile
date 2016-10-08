FROM openjdk:8u102-jre

ARG AMQ_NAME=activemq
ARG AMQ_VERSION=5.14.1
ARG AMQ_ARCHIVE_NAME=apache-$AMQ_NAME-$AMQ_VERSION-bin.tar.gz
ARG AMQ_DOWN_URL=http://archive.apache.org/dist/$AMQ_NAME/$AMQ_VERSION/$AMQ_ARCHIVE_NAME
ARG AMQ_DIR=/opt/apache-$AMQ_NAME-$AMQ_VERSION

ENV ACTIVEMQ_TCP=61616 \
#    ACTIVEMQ_AMQP=5672
#    ACTIVEMQ_STOMP=61613
#    ACTIVEMQ_MQTT=1883
#    ACTIVEMQ_WS=61614
    ACTIVEMQ_HTTP=8161 \
    ACTIVEMQ_HOME=/opt/activemq

RUN curl -O $AMQ_DOWN_URL \
    && mkdir -p /opt \
    && tar xzf $AMQ_ARCHIVE_NAME -C /opt/ \
    && rm $AMQ_ARCHIVE_NAME \
    && ln -s $AMQ_DIR $ACTIVEMQ_HOME \
    && useradd -r -M -d $ACTIVEMQ_HOME activemq \
    && chown -R activemq:activemq $AMQ_DIR

USER activemq

COPY conf/activemq.xml $ACTIVEMQ_HOME/conf/

WORKDIR $ACTIVEMQ_HOME

EXPOSE $ACTIVEMQ_TCP
EXPOSE $ACTIVEMQ_HTTP

CMD ["/bin/bash", "-c", "bin/activemq console"]