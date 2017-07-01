FROM  spotify/kafka

COPY conf/server.properties $KAFKA_HOME/config/

# 2181 is Zookeeper, 9099 is Kafka
EXPOSE 2181 9099

CMD ["supervisord", "-n"]
