package dvoraka.avservice.server.configuration.kafka;

import dvoraka.avservice.client.transport.AvNetworkComponent;
import dvoraka.avservice.client.transport.kafka.KafkaAdapter;
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
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka server configuration for the import.
 */
@Configuration
@Profile("kafka")
public class KafkaServerConfig {

    @Value("${avservice.kafka.bootstrapServers}")
    private String bootstrapServers;

    @Value("${avservice.kafka.fileTopic}")
    private String fileTopic;
    @Value("${avservice.kafka.resultTopic}")
    private String resultTopic;

    @Value("${avservice.serviceId}")
    private String serviceId;


    @Bean
    public AvNetworkComponent fileNetworkComponent(
            KafkaTemplate<String, AvMessage> kafkaTemplate,
            MessageInfoService messageInfoService
    ) {
        return new KafkaAdapter(resultTopic, serviceId, kafkaTemplate, messageInfoService);
    }

    @Bean
    public ProducerFactory<String, AvMessage> fileServerProducerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, AvMessage> fileServerKafkaTemplate() {
        return new KafkaTemplate<>(fileServerProducerFactory());
    }

    @Bean
    public ConsumerFactory<String, DefaultAvMessage> fileServerConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                new JsonDeserializer<>(DefaultAvMessage.class)
        );
    }

    @Bean
    public MessageListenerContainer fileServerMessageListenerContainer(
            ConsumerFactory<String, DefaultAvMessage> consumerFactory,
            MessageListener<String, AvMessage> fileServerMessageListener,
            ThreadPoolTaskScheduler kafkaServerThreadPoolTaskScheduler
    ) {
        ContainerProperties props = new ContainerProperties(fileTopic);
        // shouldn't be necessary but the default scheduler is not destroyed after shutdown
        props.setScheduler(kafkaServerThreadPoolTaskScheduler);

        MessageListenerContainer container = new ConcurrentMessageListenerContainer<>(
                consumerFactory,
                props
        );
        container.setupMessageListener(fileServerMessageListener);

        return container;
    }

    @Bean
    public ThreadPoolTaskScheduler kafkaServerThreadPoolTaskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        return threadPoolTaskScheduler;
    }

    @Bean
    public MessageListener<String, AvMessage> fileServerMessageListener(
            AvNetworkComponent avNetworkComponent
    ) {
        return avNetworkComponent;
    }

    private Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "server");

        return props;
    }
}
