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
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * AMQP bridge output configuration for import.
 */
public class AmqpBridgeOutputConfig {

    @Value("${avservice.amqp.host:localhost}")
    private String host;
    @Value("${avservice.amqp.vhost:antivirus}")
    private String virtualHost;

    @Value("${avservice.amqp.resultQueue:av-result}")
    private String resultQueue;
    @Value("${avservice.amqp.checkExchange:check}")
    private String checkExchange;

    @Value("${avservice.amqp.listeningTimeout:4000}")
    private long listeningTimeout;

    @Value("${avservice.amqp.user:guest}")
    private String userName;
    @Value("${avservice.amqp.pass:guest}")
    private String userPassword;

    @Value("${avservice.serviceId:default1")
    private String serviceId;


    @Bean
    public ServerComponent outComponent(
            AmqpTemplate outAmqpTemplate,
            MessageInfoService messageInfoService
    ) {
        return new AmqpComponent(checkExchange, serviceId, outAmqpTemplate, messageInfoService);
    }

    @Bean
    public ConnectionFactory outConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host);
        connectionFactory.setUsername(userName);
        connectionFactory.setPassword(userPassword);
        connectionFactory.setVirtualHost(virtualHost);

        return connectionFactory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public MessageConverter outMessageConverter() {
        return new AvMessageConverter();
    }

    @Bean
    public RabbitTemplate outAmqpTemplate(
            ConnectionFactory outConnectionFactory,
            MessageConverter outMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(outConnectionFactory);
        template.setReceiveTimeout(listeningTimeout);
        template.setRoutingKey("bridge");
        template.setQueue(resultQueue);
        template.setMessageConverter(outMessageConverter);

        return template;
    }

    @Bean
    public MessageListener outMessageListener(ServerComponent outComponent) {
        return outComponent;
    }

    @Bean
    public MessageListenerContainer outMessageListenerContainer(
            ConnectionFactory outConnectionFactory,
            MessageListener outMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(outConnectionFactory);
        container.setQueueNames(resultQueue);
        container.setMessageListener(outMessageListener);

        return container;
    }
}
