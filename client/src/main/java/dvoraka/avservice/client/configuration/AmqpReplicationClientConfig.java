package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.client.amqp.AmqpReplicationComponent;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP client configuration for the import.
 */
@Configuration
@Profile({"amqp-client", "replication"})
public class AmqpReplicationClientConfig {

    @Value("${avservice.amqp.replicationQueue}")
    private String replicationQueue;
    @Value("${avservice.amqp.replicationExchange}")
    private String replicationExchange;

    @Value("${avservice.serviceId}")
    private String serviceId;


//    @Bean
//    public ServerComponent serverComponent(
//            RabbitTemplate rabbitTemplate,
//            MessageInfoService messageInfoService
//    ) {
//        return new AmqpComponent(fileExchange, serviceId, rabbitTemplate, messageInfoService);
//    }

    @Bean
    public ReplicationComponent replicationComponent(RabbitTemplate rabbitTemplate) {
        return new AmqpReplicationComponent(rabbitTemplate);
    }

    @Bean
    public MessageListener replicationMessageListener(ReplicationComponent replicationComponent) {
        return replicationComponent;
    }

    @Bean
    public SimpleMessageListenerContainer replicationMessageListenerContainer(
            ConnectionFactory connectionFactory, MessageListener replicationMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(replicationQueue);
        container.setMessageListener(replicationMessageListener);

        return container;
    }
}
