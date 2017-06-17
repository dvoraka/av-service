FROM docker.elastic.co/logstash/logstash:5.4.1
MAINTAINER dvoraka <alen.dvorak@gmail.com>

COPY conf/av-service.conf /etc/logstash/conf.d/

USER logstash

COPY conf/jvm.options /usr/share/logstash/config/
COPY conf/logstash.yml /usr/share/logstash/config/

EXPOSE 7000

CMD ["logstash", "-f", "/etc/logstash/conf.d"]
