package dvoraka.avservice.client;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.Objects.requireNonNull;

/**
 * Kafka component.
 */
@Component
public class KafkaComponent implements ServerComponent {

    private final KafkaTemplate<String, AvMessage> kafkaTemplate;

    private static final Logger log = LogManager.getLogger(KafkaComponent.class);

    private final String topic;
    private final List<AvMessageListener> listeners;


    public KafkaComponent(String topic, KafkaTemplate<String, AvMessage> kafkaTemplate) {
        this.topic = requireNonNull(topic);
        this.kafkaTemplate = requireNonNull(kafkaTemplate);

        listeners = new CopyOnWriteArrayList<>();
    }

    @Override
    public void sendAvMessage(AvMessage message) {
        kafkaTemplate.send(topic, message);
    }

    @Override
    public void onMessage(ConsumerRecord<String, AvMessage> record) {
        System.out.println(record);
        AvMessage avMessage = record.value();

        notifyListeners(listeners, avMessage);
    }

    @Override
    public String getServiceId() {
        return "";
    }

    @Override
    public void addAvMessageListener(AvMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeAvMessageListener(AvMessageListener listener) {
        listeners.remove(listener);
    }
}
