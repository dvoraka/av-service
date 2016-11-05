package dvoraka.avservice.server.configuration.amqp;

import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.amqp.AmqpComponent;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
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

    @Value("${avservice.amqp.checkQueue:av-check}")
    private String checkQueue;
    @Value("${avservice.amqp.resultQueue:av-result}")
    private String resultQueue;

    @Value("${avservice.amqp.checkExchange:check}")
    private String checkExchange;
    @Value("${avservice.amqp.resultExchange:result}")
    private String resultExchange;

    @Value("${avservice.serviceId:default1")
    private String serviceId;


    @Bean
    public AvServer avServer() {
        return new BasicAvServer(serviceId);
    }

    @Bean
    public ServerComponent serverComponent(
            AmqpTemplate amqpTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpComponent(resultExchange, serviceId, amqpTemplate, messageInfoService);
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(
            ConnectionFactory connectionFactory, MessageListener messageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(checkQueue);
        container.setMessageListener(messageListener);

        return container;
    }

    @Bean
    public MessageListener messageListener(ServerComponent serverComponent) {
        return serverComponent;
    }

    @Bean
    public Queue checkQueue() {
        return new Queue(checkQueue);
    }

    @Bean
    public Queue resultQueue() {
        return new Queue(resultQueue);
    }

    @Bean
    public FanoutExchange checkExchange() {
        return new FanoutExchange(checkExchange);
    }

    @Bean
    public FanoutExchange resultExchange() {
        return new FanoutExchange(resultExchange);
    }

    @Bean
    public Binding bindingCheck(Queue checkQueue, FanoutExchange checkExchange) {
        return BindingBuilder.bind(checkQueue).to(checkExchange);
    }

    @Bean
    public Binding bindingResult(Queue resultQueue, FanoutExchange resultExchange) {
        return BindingBuilder.bind(resultQueue).to(resultExchange);
    }
}
