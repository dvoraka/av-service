package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.NetworkComponent;
import dvoraka.avservice.client.amqp.AmqpAdapter;
import dvoraka.avservice.db.service.MessageInfoService;
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
 * AMQP file client configuration for the import.
 */
@Configuration
@Profile("amqp")
public class AmqpFileClientConfig {

    @Value("${avservice.amqp.resultQueue}")
    private String resultQueue;
    @Value("${avservice.amqp.fileExchange}")
    private String fileExchange;

    @Value("${avservice.serviceId}")
    private String serviceId;


    @Bean
    public NetworkComponent serverComponent(
            RabbitTemplate rabbitTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpAdapter(fileExchange, serviceId, rabbitTemplate, messageInfoService);
    }

    @Bean
    public MessageListener messageListener(NetworkComponent networkComponent) {
        return networkComponent;
    }

    @Bean
    public MessageListenerContainer messageListenerContainer(
            ConnectionFactory connectionFactory, MessageListener messageListener
    ) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(resultQueue);
        container.setMessageListener(messageListener);

        return container;
    }
}
