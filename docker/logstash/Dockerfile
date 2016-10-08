FROM logstash:2.4.0-1

COPY conf/av-service.conf /etc/logstash/conf.d/

EXPOSE 7000

CMD ["logstash", "-f", "/etc/logstash/conf.d", "agent"]
