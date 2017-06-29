package dvoraka.avservice.client;

import dvoraka.avservice.common.AvMessageListener;
import dvoraka.avservice.common.data.AvMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * Kafka component.
 */
@Component
public class KafkaComponent implements ServerComponent {

    private final KafkaTemplate<String, AvMessage> kafkaTemplate;


    public KafkaComponent(KafkaTemplate<String, AvMessage> kafkaTemplate) {
        this.kafkaTemplate = requireNonNull(kafkaTemplate);
    }

    @Override
    public void sendAvMessage(AvMessage message) {
        kafkaTemplate.send("avcheck.t", message);
    }

    @Override
    public String getServiceId() {
        return null;
    }

    @Override
    public void addAvMessageListener(AvMessageListener listener) {

    }

    @Override
    public void removeAvMessageListener(AvMessageListener listener) {

    }

    @KafkaListener(topics = "avcheck.t")
    public void onMessage(AvMessage message) {
        System.out.println(message);
    }
}
