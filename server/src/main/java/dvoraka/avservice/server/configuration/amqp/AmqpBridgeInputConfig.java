package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.amqp.AmqpComponent;
import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.common.amqp.AvMessageMapper;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AMQP bridge input configuration for import.
 */
@Configuration
public class AmqpBridgeInputConfig {

    @Value("${avservice.amqp.host:localhost}")
    private String host;
    @Value("${avservice.amqp.vhost:antivirus}")
    private String virtualHost;

    @Value("${avservice.amqp.fileQueue}")
    private String fileQueue;
    @Value("${avservice.amqp.resultExchange}")
    private String resultExchange;
    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;

    @Value("${avservice.amqp.user}")
    private String userName;
    @Value("${avservice.amqp.pass}")
    private String userPassword;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public AvMessageMapper avMessageMapper() {
        return new AvMessageMapper();
    }

    @Bean
    public MessageConverter inMessageConverter(AvMessageMapper messageMapper) {
        return new AvMessageConverter(messageMapper);
    }

    @Bean
    public ConnectionFactory inConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(userPassword);
        connectionFactory.setVirtualHost(virtualHost);

        return connectionFactory;
    }

    @Bean
    public AmqpAdmin inAmqpAdmin(ConnectionFactory inConnectionFactory) {
        return new RabbitAdmin(inConnectionFactory);
    }

    @Bean
    public RabbitTemplate inRabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReceiveTimeout(listeningTimeout);
        template.setMessageConverter(messageConverter);

        return template;
    }

    @Bean
    public ServerComponent inComponent(
            RabbitTemplate inRabbitTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpComponent(resultExchange, serviceId, inRabbitTemplate, messageInfoService);
    }

    @Bean
    public MessageListener inMessageListener(ServerComponent inComponent) {
        return inComponent;
    }

    @Bean
    public SimpleMessageListenerContainer inMessageListenerContainer(
            ConnectionFactory connectionFactory,
            MessageListener inMessageListener
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(fileQueue);
        container.setMessageListener(inMessageListener);

        return container;
    }
}
