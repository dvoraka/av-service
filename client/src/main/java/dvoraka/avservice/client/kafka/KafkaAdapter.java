package dvoraka.avservice.client.kafka;

import dvoraka.avservice.client.AbstractNetworkComponent;
import dvoraka.avservice.client.AvNetworkComponent;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.InfoSource;
import dvoraka.avservice.common.listener.AvMessageListener;
import dvoraka.avservice.db.service.MessageInfoService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static java.util.Objects.requireNonNull;

/**
 * Kafka network component adapter.
 */
@Component
public class KafkaAdapter extends AbstractNetworkComponent<AvMessage, AvMessageListener>
        implements AvNetworkComponent {

    private final String topic;
    private final String serviceId;
    private final KafkaTemplate<String, AvMessage> kafkaTemplate;
    private final MessageInfoService messageInfoService;


    @Autowired
    public KafkaAdapter(
            String topic,
            String serviceId,
            KafkaTemplate<String, AvMessage> kafkaTemplate,
            MessageInfoService messageInfoService
    ) {
        this.topic = requireNonNull(topic);
        this.serviceId = requireNonNull(serviceId);
        this.kafkaTemplate = requireNonNull(kafkaTemplate);
        this.messageInfoService = requireNonNull(messageInfoService);
    }

    @Override
    public void onMessage(ConsumerRecord<String, AvMessage> record) {
        requireNonNull(record, "Record must not be null!");
        log.debug("On message: {}", record);

        AvMessage avMessage = record.value();

        messageInfoService.save(avMessage, InfoSource.KAFKA_ADAPTER_IN, serviceId);

        notifyListeners(getListeners(), avMessage);
    }

    @Override
    public void sendMessage(AvMessage message) {
        requireNonNull(message, "Message must not be null!");
        log.debug("Send: {}", message);

        kafkaTemplate.send(topic, message);

        messageInfoService.save(message, InfoSource.KAFKA_ADAPTER_OUT, serviceId);
    }

    @Override
    public String getServiceId() {
        return serviceId;
    }
}
