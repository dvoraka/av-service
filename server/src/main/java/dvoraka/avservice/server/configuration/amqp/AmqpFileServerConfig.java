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
 * AMQP file server configuration for import.
 */
@Configuration
@Profile("amqp-file-server")
public class AmqpFileServerConfig {

    @Value("${avservice.amqp.fileQueue}")
    private String fileQueue;

    @Value("${avservice.amqp.resultExchange}")
    private String resultExchange;

    @Value("${avservice.serviceId}")
    private String serviceId;


    @Bean
    public AvServer avServer(
            ServerComponent serverComponent,
            MessageProcessor checkAndFileProcessor,
            MessageInfoService messageInfoService
    ) {
        return new BasicAvServer(
                serviceId,
                serverComponent,
                checkAndFileProcessor,
                messageInfoService
        );
    }

    @Bean
    public ServerComponent serverComponent(
            RabbitTemplate rabbitTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpComponent(
                resultExchange, serviceId, rabbitTemplate, messageInfoService);
    }

    @Bean
    public MessageListenerContainer messageListenerContainer(
            ConnectionFactory connectionFactory, MessageListener messageListener
    ) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(fileQueue);
        container.setMessageListener(messageListener);

        return container;
    }

    @Bean
    public MessageListener messageListener(ServerComponent serverComponent) {
        return serverComponent;
    }
}
