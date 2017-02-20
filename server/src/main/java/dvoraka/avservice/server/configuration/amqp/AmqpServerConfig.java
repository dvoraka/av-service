package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.amqp.AmqpComponent;
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
 * AMQP server configuration for import.
 */
@Configuration
@Profile("amqp-server")
public class AmqpServerConfig {

    @Value("${avservice.amqp.checkQueue}")
    private String checkQueue;

    @Value("${avservice.amqp.resultExchange}")
    private String resultExchange;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public AvServer avServer(
            ServerComponent serverComponent,
            MessageProcessor checkMessageProcessor,
            MessageInfoService messageInfoService
    ) {
        return new BasicAvServer(
                serviceId,
                serverComponent,
                checkMessageProcessor,
                messageInfoService
        );
    }

    @Bean
    public ServerComponent serverComponent(
            RabbitTemplate rabbitTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpComponent(resultExchange, serviceId, rabbitTemplate, messageInfoService);
    }

    @Bean
    public MessageListenerContainer messageListenerContainer(
            ConnectionFactory connectionFactory, MessageListener messageListener) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(checkQueue);
        container.setMessageListener(messageListener);

        return container;
    }

    @Bean
    public MessageListener messageListener(ServerComponent serverComponent) {
        return serverComponent;
    }
}
