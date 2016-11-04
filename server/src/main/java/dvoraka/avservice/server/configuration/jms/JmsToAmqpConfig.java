package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.amqp.AmqpComponent;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * JMS to AMQP bridge Spring configuration.
 */
@Configuration
@Import({
        ServiceConfig.class,
        JmsBridgeInputConfig.class
})
@Profile("jms2amqp")
public class JmsToAmqpConfig {

    // AMQP
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
    public ServerComponentBridge componentBridge(
            ServerComponent inComponent, ServerComponent outComponent) {
        return new ServerComponentBridge(inComponent, outComponent);
    }

    //
    // AMQP
    //
    @Bean
    public ServerComponent outComponent() {
        return new AmqpComponent(checkExchange, serviceId);
    }

    @Bean
    public SimpleMessageListenerContainer outMessageListenerContainer(
            ConnectionFactory outConnectionFactory,
            MessageListener outMessageListener
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(outConnectionFactory);
        container.setQueueNames(resultQueue);
        container.setMessageListener(outMessageListener);

        return container;
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
    public RabbitTemplate amqpTemplate(
            ConnectionFactory connectionFactory, MessageConverter outMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
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
    public Queue resultQueue() {
        return new Queue(resultQueue);
    }

    @Bean
    public FanoutExchange checkExchange() {
        return new FanoutExchange(checkExchange);
    }

    @Bean
    public MessageConverter outMessageConverter() {
        return new AvMessageConverter();
    }
}
