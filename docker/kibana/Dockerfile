FROM docker.elastic.co/kibana/kibana:5.4.1
MAINTAINER dvoraka <alen.dvorak@gmail.com>

ENV ELASTICSEARCH_URL=http://localhost:9200

COPY conf/kibana.yml /usr/share/kibana/config/

EXPOSE 5601

CMD ["kibana"]
