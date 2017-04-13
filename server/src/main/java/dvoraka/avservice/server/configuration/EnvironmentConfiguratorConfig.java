package dvoraka.avservice.server.configuration;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Environment configuration configuration. Currently it is for AMQP only.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
public class EnvironmentConfiguratorConfig {

    @Value("${avservice.amqp.host}")
    private String host;
    @Value("${avservice.amqp.vhost}")
    private String virtualHost;

    @Value("${avservice.amqp.user}")
    private String userName;
    @Value("${avservice.amqp.pass}")
    private String userPassword;

    @Value("${avservice.amqp.fileQueue}")
    private String fileQueue;
    @Value("${avservice.amqp.resultQueue}")
    private String resultQueue;
    @Value("${avservice.amqp.replicationQueue}")
    private String replicationQueue;

    @Value("${avservice.amqp.fileExchange}")
    private String fileExchange;
    @Value("${avservice.amqp.resultExchange}")
    private String resultExchange;
    @Value("${avservice.amqp.replicationExchange}")
    private String replicationExchange;


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

    @Bean
    public MessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(fileQueue);

        return container;
    }

    @Bean
    public Queue fileQueue() {
        return new Queue(fileQueue);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(resultQueue);
    }

    @Bean
    public Queue replicationQueue() {
        return new Queue(replicationQueue);
    }

    @Bean
    public FanoutExchange fileExchange() {
        return new FanoutExchange(fileExchange);
    }

    @Bean
    public FanoutExchange resultExchange() {
        return new FanoutExchange(resultExchange);
    }

    @Bean
    public FanoutExchange replicationExchange() {
        return new FanoutExchange(replicationExchange);
    }

    @Bean
    public Binding bindingFile(Queue fileQueue, FanoutExchange fileExchange) {
        return BindingBuilder
                .bind(fileQueue)
                .to(fileExchange);
    }

    @Bean
    public Binding bindingResult(Queue resultQueue, FanoutExchange resultExchange) {
        return BindingBuilder
                .bind(resultQueue)
                .to(resultExchange);
    }

    @Bean
    public Binding bindingReplication(Queue replicationQueue, FanoutExchange replicationExchange) {
        return BindingBuilder
                .bind(replicationQueue)
                .to(replicationExchange);
    }
}
