package dvoraka.avservice.configuration;

import dvoraka.avservice.server.AVServer;
import dvoraka.avservice.server.AmqpAVServer;
import dvoraka.avservice.server.ListeningStrategy;
import dvoraka.avservice.server.ReceivingType;
import dvoraka.avservice.server.SimpleAmqpListeningStrategy;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP Spring configuration.
 */
@Configuration
@Profile("amqp")
public class AmqpConfig {

    String hostname = "localhost";
    String virtualHost = "antivirus";
    String checkQueueName = "av-check";
    String resultQueueName = "av-result";
    String checkExchangeName = "check";
    String resultExchangeName = "result";

    String userName = "guest";
    String userPassword = "guest";

    @Bean
    public AVServer avServer() {
        return new AmqpAVServer(ReceivingType.LISTENER);
    }

    @Bean
    public ListeningStrategy listeningStrategy() {
        return new SimpleAmqpListeningStrategy();
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(hostname);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(userPassword);
        connectionFactory.setVirtualHost(virtualHost);

        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin() {
        return new RabbitAdmin(connectionFactory());
    }

    @Bean
    public RabbitTemplate amqpTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory());
        // wait forever
        template.setReceiveTimeout(-1);
        template.setRoutingKey("test");
        template.setQueue(checkQueueName);

        return template;
    }

    @Bean
    public Queue checkQueue() {
        return new Queue(checkQueueName);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(resultQueueName);
    }

    @Bean
    public FanoutExchange checkExchange() {
        return new FanoutExchange(checkExchangeName);
    }

    @Bean
    public FanoutExchange resultExchange() {
        return new FanoutExchange(resultExchangeName);
    }

    @Bean
    Binding bindingCheck(Queue checkQueue, FanoutExchange checkExchange) {
        return BindingBuilder.bind(checkQueue).to(checkExchange);
    }

    @Bean
    Binding bindingResult(Queue resultQueue, FanoutExchange resultExchange) {
        return BindingBuilder.bind(resultQueue).to(resultExchange);
    }
}
