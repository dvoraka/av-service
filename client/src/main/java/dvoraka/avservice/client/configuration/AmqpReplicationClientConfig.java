package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.client.amqp.AmqpReplicationComponent;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP client configuration for the import.
 */
@Configuration
@Profile("replication")
public class AmqpReplicationClientConfig {

    @Value("${avservice.amqp.replicationQueue}")
    private String replicationQueueName;
    @Value("${avservice.amqp.replicationExchange}")
    private String replicationExchange;

    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;

    @Value("${avservice.serviceId}")
    private String serviceId;
    @Value("${avservice.storage.replication.nodeId}")
    private String nodeId;

    @Value("${avservice.amqp.replicationQueue}.${avservice.storage.replication.nodeId}")
    private String fullQueueName;


    @Bean
    public ReplicationComponent replicationComponent(RabbitTemplate rabbitTemplate) {
        return new AmqpReplicationComponent(rabbitTemplate, nodeId);
    }

    @Bean
    public MessageListener replicationMessageListener(ReplicationComponent replicationComponent) {
        return replicationComponent;
    }

    @Bean
    public SimpleMessageListenerContainer replicationMessageListenerContainer(
            ConnectionFactory connectionFactory,
            MessageListener replicationMessageListener
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(fullQueueName);
        container.setMessageListener(replicationMessageListener);

        return container;
    }

    @Bean
    public MessageConverter replicationMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue replicationQueue() {
        return new Queue(fullQueueName, false, true, true);
    }

    @Bean
    public Binding bindReplication(Queue replicationQueue) {
        return BindingBuilder
                .bind(replicationQueue)
                .to(new FanoutExchange(replicationExchange));
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter replicationMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReceiveTimeout(listeningTimeout);
        template.setExchange(replicationExchange);
        template.setMessageConverter(replicationMessageConverter);

        return template;
    }
}
