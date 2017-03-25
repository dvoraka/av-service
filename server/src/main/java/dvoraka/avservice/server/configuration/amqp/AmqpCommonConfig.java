package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.server.configuration.ServerCommonConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * AMQP common configuration for import.
 */
@Configuration
@Import({ServerCommonConfig.class})
@Profile("amqp")
public class AmqpCommonConfig {

    @Value("${avservice.amqp.host:localhost}")
    private String host;

    @Value("${avservice.amqp.vhost:antivirus}")
    private String virtualHost;

    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;

    @Value("${avservice.amqp.user:guest}")
    private String userName;
    @Value("${avservice.amqp.pass:guest}")
    private String userPassword;


//    @Bean
//    public AvMessageMapper avMessageMapper() {
//        return new AvMessageMapper();
//    }
//
//    @Bean
//    public MessageConverter messageConverter(AvMessageMapper messageMapper) {
//        return new AvMessageConverter(messageMapper);
//    }
//
//    @Bean
//    public ConnectionFactory connectionFactory() {
//        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
//        connectionFactory.setUsername(userName);
//        connectionFactory.setPassword(userPassword);
//        connectionFactory.setVirtualHost(virtualHost);
//
//        return connectionFactory;
//    }
//
//    @Bean
//    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
//        return new RabbitAdmin(connectionFactory);
//    }
//
//    @Bean
//    public RabbitTemplate rabbitTemplate(
//            ConnectionFactory connectionFactory,
//            MessageConverter messageConverter) {
//        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//        template.setReceiveTimeout(listeningTimeout);
//        template.setRoutingKey("test");
//        template.setMessageConverter(messageConverter);
//
//        return template;
//    }
}
