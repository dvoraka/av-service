package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.amqp.AmqpComponent;
import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.common.amqp.AvMessageMapper;
import dvoraka.avservice.db.service.MessageInfoService;
import org.springframework.amqp.core.MessageListener;
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

    @Value("${avservice.amqp.fileQueue}")
    private String fileQueue;
    @Value("${avservice.amqp.resultExchange}")
    private String resultExchange;

    @Value("${avservice.serviceId}")
    private String serviceId;

    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;


    @Bean
    public ServerComponent fileServerComponent(
            RabbitTemplate fileServerRabbitTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpComponent(
                resultExchange, serviceId, fileServerRabbitTemplate, messageInfoService);
    }

    @Bean
    public AvMessageMapper fileServerMessageMapper() {
        return new AvMessageMapper();
    }

    @Bean
    public MessageConverter fileServerMessageConverter(AvMessageMapper fileServerMessageMapper) {
        return new AvMessageConverter(fileServerMessageMapper);
    }

    @Bean
    public RabbitTemplate fileServerRabbitTemplate(
            ConnectionFactory serverConnectionFactory,
            MessageConverter fileServerMessageConverter
    ) {
        RabbitTemplate template = new RabbitTemplate(serverConnectionFactory);
        template.setReceiveTimeout(listeningTimeout);
        template.setMessageConverter(fileServerMessageConverter);

        return template;
    }

    @Bean
    public MessageListener fileServerMessageListener(ServerComponent fileServerComponent) {
        return fileServerComponent;
    }

    @Bean
    public MessageListenerContainer fileServerMessageListenerContainer(
            ConnectionFactory serverConnectionFactory,
            MessageListener fileServerMessageListener
    ) {
        DirectMessageListenerContainer container = new DirectMessageListenerContainer();
        container.setConnectionFactory(serverConnectionFactory);
        container.setQueueNames(fileQueue);
        container.setMessageListener(fileServerMessageListener);

        return container;
    }
}
