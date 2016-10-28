package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.ServerComponentBridge;
import dvoraka.avservice.server.amqp.AmqpComponent;
import dvoraka.avservice.server.jms.JmsComponent;
import org.apache.activemq.ActiveMQConnectionFactory;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;

import javax.annotation.PostConstruct;

/**
 * JMS to AMQP bridge Spring configuration.
 */
@Configuration
@Import({ServiceConfig.class})
@Profile("jms2amqp")
public class JmsToAmqpConfig {

    @Autowired
    private Environment env;

    // JMS
    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;

    @Value("${avservice.jms.checkDestination:check}")
    private String checkDestination;
    @Value("${avservice.jms.resultDestination:result}")
    private String resultDestination;

    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;

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

    private String serviceId;


    @PostConstruct
    public void init() {
        serviceId = env.getProperty("avservice.serviceId", "default1");
    }

    @Bean
    public ServerComponentBridge componentBridge(
            ServerComponent inComponent, ServerComponent outComponent) {
        return new ServerComponentBridge(inComponent, outComponent);
    }

    //
    // JMS
    //

    @Bean
    public ServerComponent inComponent() {
        return new JmsComponent(resultDestination, serviceId);
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);

        return factory;
    }

    @Bean
    public org.springframework.jms.listener.SimpleMessageListenerContainer
    inMessageListenerContainer() {
        org.springframework.jms.listener.SimpleMessageListenerContainer container =
                new org.springframework.jms.listener.SimpleMessageListenerContainer();
        container.setConnectionFactory(activeMQConnFactory());
        container.setDestinationName(checkDestination);
        container.setMessageListener(inMessageListener());

        return container;
    }

    @Bean
    public javax.jms.ConnectionFactory
    inConnectionFactory(javax.jms.ConnectionFactory activeMQConnFactory) {
        javax.jms.ConnectionFactory factory =
                new org.springframework.jms.connection.CachingConnectionFactory(
                        activeMQConnFactory);

        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(
            javax.jms.ConnectionFactory inConnectionFactory,
            org.springframework.jms.support.converter.MessageConverter inMessageConverter
    ) {
        JmsTemplate template = new JmsTemplate(inConnectionFactory);
        template.setReceiveTimeout(receiveTimeout);
        template.setMessageConverter(inMessageConverter);

        return template;
    }

    @Bean
    public org.springframework.jms.support.converter.MessageConverter inMessageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("typeId");

        return messageConverter;
    }

    @Bean
    public MessageListener inMessageListener() {
        return inComponent();
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
