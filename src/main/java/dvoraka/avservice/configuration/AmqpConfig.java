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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP Spring configuration.
 */
@Configuration
@Profile("amqp")
public class AmqpConfig {

    @Value("${avservice.amqp.host:localhost}")
    String host;

    @Value("${avservice.amqp.vhost:antivirus}")
    String virtualHost;

    @Value("${avservice.amqp.checkQueue:av-check}")
    String checkQueue;

    @Value("${avservice.amqp.resultQueue:av-result}")
    String resultQueue;

    @Value("${avservice.amqp.checkExchange:check}")
    String checkExchange;

    @Value("${avservice.amqp.resultExchange:result}")
    String resultExchange;

    @Value("${avservice.amqp.user:guest}")
    String userName;
    @Value("${avservice.amqp.pass:guest}")
    String userPassword;


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
                new CachingConnectionFactory(host);
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
        template.setQueue(checkQueue);

        return template;
    }

    @Bean
    public Queue checkQueue() {
        return new Queue(checkQueue);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(resultQueue);
    }

    @Bean
    public FanoutExchange checkExchange() {
        return new FanoutExchange(checkExchange);
    }

    @Bean
    public FanoutExchange resultExchange() {
        return new FanoutExchange(resultExchange);
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
