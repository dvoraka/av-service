package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ReplicationComponent;
import dvoraka.avservice.client.amqp.AmqpReplicationComponent;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP replication test client configuration for the import.
 */
@Configuration
@Profile("replication-test")
public class TestAmqpReplicationClientConfig {

    @Value("${avservice.amqp.host}")
    private String host;
    @Value("${avservice.amqp.vhost}")
    private String virtualHost;

    @Value("${avservice.amqp.user}")
    private String userName;
    @Value("${avservice.amqp.pass}")
    private String userPassword;

    @Value("${avservice.amqp.replicationExchange}")
    private String replicationExchange;

    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;

    @Value("${avservice.storage.replication.testNodeId}")
    private String testNodeId;
    @Value("${avservice.amqp.replicationQueue}.${avservice.storage.replication.testNodeId}")
    private String fullQueueName;


    @Bean
    public ReplicationComponent replicationComponent(RabbitTemplate replicationRabbitTemplate) {
        return new AmqpReplicationComponent(replicationRabbitTemplate, testNodeId);
    }

    @Bean
    public MessageListener replicationMessageListener(ReplicationComponent replicationComponent) {
        return replicationComponent;
    }

    @Bean
    public MessageListenerContainer replicationMessageListenerContainer(
            ConnectionFactory connectionFactory,
            MessageListener replicationMessageListener
    ) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
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
    public RabbitTemplate replicationRabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter replicationMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReceiveTimeout(listeningTimeout);
        template.setExchange(replicationExchange);
        template.setMessageConverter(replicationMessageConverter);

        return template;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(userPassword);
        connectionFactory.setVirtualHost(virtualHost);

        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
