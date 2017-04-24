package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.amqp.AmqpComponent;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.DirectMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * AMQP server configuration for the import.
 */
@Configuration
@Profile("amqp-server")
public class AmqpServerConfig {

    @Value("${avservice.amqp.fileQueue}")
    private String fileQueue;
    @Value("${avservice.amqp.resultExchange}")
    private String resultExchange;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public AvServer avServer(
            ServerComponent avServerComponent,
            MessageProcessor checkMessageProcessor,
            MessageInfoService messageInfoService
    ) {
        return new BasicAvServer(
                serviceId,
                avServerComponent,
                checkMessageProcessor,
                messageInfoService
        );
    }

    @Bean
    public ServerComponent avServerComponent(
            RabbitTemplate fileServerRabbitTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpComponent(
                resultExchange, serviceId, fileServerRabbitTemplate, messageInfoService);
    }

    @Bean
    public MessageListenerContainer avMessageListenerContainer(
            ConnectionFactory serverConnectionFactory, MessageListener avMessageListener
    ) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
        container.setConnectionFactory(serverConnectionFactory);
        container.setQueueNames(fileQueue);
        container.setMessageListener(avMessageListener);

        return container;
    }

    @Bean
    public MessageListener avMessageListener(ServerComponent avServerComponent) {
        return avServerComponent;
    }
}
