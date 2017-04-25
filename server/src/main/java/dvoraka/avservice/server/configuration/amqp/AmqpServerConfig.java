package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.amqp.AmqpComponent;
import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.common.amqp.AvMessageMapper;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP server configuration for the import.
 */
@Configuration
@Profile("amqp")
public class AmqpServerConfig {

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
    @Value("${avservice.amqp.resultExchange}")
    private String resultExchange;

    @Value("${avservice.serviceId}")
    private String serviceId;

    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;


    @Bean
    public AvServer fileServer(
            ServerComponent fileServerComponent,
            MessageProcessor messageProcessor,
            MessageInfoService messageInfoService
    ) {
        return new BasicAvServer(
                serviceId,
                fileServerComponent,
                messageProcessor,
                messageInfoService
        );
    }

    @Bean
    public ServerComponent fileServerComponent(
            RabbitTemplate fileServerRabbitTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpComponent(
                resultExchange, serviceId, fileServerRabbitTemplate, messageInfoService);
    }

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

    @Bean
    public MessageListenerContainer fileMessageListenerContainer(
            ConnectionFactory serverConnectionFactory, MessageListener fileMessageListener
    ) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
        container.setConnectionFactory(serverConnectionFactory);
        container.setQueueNames(fileQueue);
        container.setMessageListener(fileMessageListener);

        return container;
    }

    @Bean
    public MessageListener fileMessageListener(ServerComponent fileServerComponent) {
        return fileServerComponent;
    }
}
