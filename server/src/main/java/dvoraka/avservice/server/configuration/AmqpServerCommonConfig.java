package dvoraka.avservice.server.configuration;

import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.common.amqp.AvMessageMapper;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP server common configuration.
 */
@Configuration
@Profile("amqp")
public class AmqpServerCommonConfig {

    @Value("${avservice.amqp.host}")
    private String host;
    @Value("${avservice.amqp.vhost}")
    private String virtualHost;

    @Value("${avservice.amqp.user}")
    private String userName;
    @Value("${avservice.amqp.pass}")
    private String userPassword;

    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;


    @Bean
    public ConnectionFactory serverConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(userPassword);
        connectionFactory.setVirtualHost(virtualHost);

        return connectionFactory;
    }

    @Bean
    public AvMessageMapper fileServerMessageMapper() {
        return new AvMessageMapper();
    }

    @Bean
    public MessageConverter serverMessageConverter(AvMessageMapper fileServerMessageMapper) {
        return new AvMessageConverter(fileServerMessageMapper);
    }

    @Bean
    public RabbitTemplate fileServerRabbitTemplate(
            ConnectionFactory serverConnectionFactory,
            MessageConverter serverMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(serverConnectionFactory);
        template.setReceiveTimeout(listeningTimeout);
        template.setMessageConverter(serverMessageConverter);

        return template;
    }
}
