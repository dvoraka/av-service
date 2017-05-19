package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.QueueCleaner;
import dvoraka.avservice.client.amqp.AmqpQueueCleaner;
import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.common.amqp.AvMessageMapper;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP file common configuration for the import.
 */
@Configuration
@Profile("amqp")
public class AmqpFileCommonConfig {

    @Value("${avservice.amqp.host}")
    private String host;
    @Value("${avservice.amqp.vhost}")
    private String virtualHost;

    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;

    @Value("${avservice.amqp.user}")
    private String userName;
    @Value("${avservice.amqp.pass}")
    private String userPassword;


    @Bean
    public AvMessageMapper fileMessageMapper() {
        return new AvMessageMapper();
    }

    @Bean
    public MessageConverter messageConverter(AvMessageMapper fileMessageMapper) {
        return new AvMessageConverter(fileMessageMapper);
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

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReceiveTimeout(listeningTimeout);
        template.setMessageConverter(messageConverter);

        return template;
    }

    @Bean
    public QueueCleaner queueCleaner(AmqpTemplate rabbitTemplate) {
        return new AmqpQueueCleaner(rabbitTemplate);
    }
}
