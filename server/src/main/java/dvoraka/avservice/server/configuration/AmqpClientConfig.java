package dvoraka.avservice.server.configuration;

import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.server.amqp.AmqpClient;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

/**
 * AMQP client Spring configuration.
 */
@Configuration
@PropertySource("classpath:avservice.properties")
@Profile("amqp-client")
public class AmqpClientConfig {

    @Autowired
    private Environment env;


    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(env.getProperty("avservice.amqp.host", "localhost"));
        connectionFactory.setUsername(env.getProperty("avservice.amqp.user", "user"));
        connectionFactory.setPassword(env.getProperty("avservice.amqp.pass", "guest"));
        connectionFactory.setVirtualHost(env.getProperty("avservice.amqp.vhost", "antivirus"));

        return connectionFactory;
    }

    @Bean
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        final long receiveTimeout = 1000L;
        template.setReceiveTimeout(receiveTimeout);
        template.setRoutingKey("test");
        template.setQueue(env.getProperty("avservice.amqp.resultQueue"));

        return template;
    }

    @Bean
    public AmqpClient amqpClient() {
        return new AmqpClient(env.getProperty("avservice.amqp.checkExchange", "check"));
    }

    @Bean
    public MessageConverter messageConverter() {
        return new AvMessageConverter();
    }
}
