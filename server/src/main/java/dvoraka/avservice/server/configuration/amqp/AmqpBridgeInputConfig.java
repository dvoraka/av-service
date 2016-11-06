package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.amqp.AmqpComponent;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
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

    @Value("${avservice.amqp.checkQueue:av-check}")
    private String checkQueue;
    @Value("${avservice.amqp.resultExchange:result}")
    private String resultExchange;
    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;

    @Value("${avservice.amqp.user:guest}")
    private String userName;
    @Value("${avservice.amqp.pass:guest}")
    private String userPassword;

    @Value("${avservice.serviceId:default1")
    private String serviceId;


    @Bean
    public MessageConverter inMessageConverter() {
        return new AvMessageConverter();
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
    public AmqpTemplate inAmqpTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReceiveTimeout(listeningTimeout);
        template.setMessageConverter(messageConverter);

        return template;
    }

    @Bean
    public ServerComponent inServerComponent(
            AmqpTemplate inAmqpTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpComponent(resultExchange, serviceId, inAmqpTemplate, messageInfoService);
    }

    @Bean
    public MessageListener inMessageListener(ServerComponent inServerComponent) {
        return inServerComponent;
    }

    @Bean
    public SimpleMessageListenerContainer inMessageListenerContainer(
            ConnectionFactory connectionFactory, MessageListener inMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(checkQueue);
        container.setMessageListener(inMessageListener);

        return container;
    }
}
