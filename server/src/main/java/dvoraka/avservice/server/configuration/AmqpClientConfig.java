package dvoraka.avservice.server.configuration;

import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.server.amqp.AmqpClient;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;


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
        template.setReceiveTimeout(listeningTimeout);
        template.setRoutingKey("test");

        return template;
    }

    @Bean
    public AmqpClient amqpClient() {
        return new AmqpClient();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new AvMessageConverter();
    }
}
