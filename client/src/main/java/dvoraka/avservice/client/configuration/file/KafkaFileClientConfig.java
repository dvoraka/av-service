package dvoraka.avservice.client.configuration.file;

import dvoraka.avservice.client.AvNetworkComponent;
import dvoraka.avservice.client.kafka.KafkaAdapter;
import dvoraka.avservice.common.data.AvMessage;
import dvoraka.avservice.common.data.DefaultAvMessage;
import dvoraka.avservice.db.service.MessageInfoService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration.
 */
@Configuration
@Profile("kafka")
public class KafkaFileClientConfig {

    @Value("${avservice.kafka.broker}")
    private String broker;

    @Value("${avservice.kafka.fileTopic}")
    private String fileTopic;
    @Value("${avservice.kafka.resultTopic}")
    private String resultTopic;

    @Value("${avservice.serviceId}")
    private String serviceId;


    @Bean
    public AvNetworkComponent serverComponent(
            KafkaTemplate<String, AvMessage> kafkaTemplate,
            MessageInfoService messageInfoService
    ) {
        return new KafkaAdapter(fileTopic, serviceId, kafkaTemplate, messageInfoService);
    }

    @Bean
    public ProducerFactory<String, AvMessage> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AvMessage> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<String, DefaultAvMessage> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(DefaultAvMessage.class)
        );
    }

    @Bean
    public MessageListenerContainer messageListenerContainer(
            ConsumerFactory<String, DefaultAvMessage> consumerFactory,
            MessageListener<String, AvMessage> messageListener
    ) {
        ContainerProperties props = new ContainerProperties(resultTopic);
        MessageListenerContainer container = new ConcurrentMessageListenerContainer<>(
                consumerFactory,
                props
        );
        container.setupMessageListener(messageListener);

        return container;
    }

    @Bean
    public MessageListener<String, AvMessage> messageListener(AvNetworkComponent avNetworkComponent) {
        return avNetworkComponent;
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "client");

        return props;
    }
}
