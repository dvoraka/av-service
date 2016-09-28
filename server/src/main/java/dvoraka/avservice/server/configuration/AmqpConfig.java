package dvoraka.avservice.server.configuration;

import dvoraka.avservice.common.amqp.AvMessageConverter;
import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.amqp.AmqpClient;
import dvoraka.avservice.server.amqp.AmqpComponent;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
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

import javax.annotation.PostConstruct;

/**
 * AMQP Spring configuration.
 */
@Configuration
@Import({ServiceConfig.class})
@Profile("amqp")
public class AmqpConfig {

    @Autowired
    private Environment env;

    @Value("${avservice.amqp.host:localhost}")
    private String host;

    @Value("${avservice.amqp.vhost:antivirus}")
    private String virtualHost;

    @Value("${avservice.amqp.checkQueue:av-check}")
    private String checkQueue;

    @Value("${avservice.amqp.resultQueue:av-result}")
    private String resultQueue;

    @Value("${avservice.amqp.checkExchange:check}")
    private String checkExchange;

    @Value("${avservice.amqp.resultExchange:result}")
    private String resultExchange;

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
    public AvServer avServer() {
        return new BasicAvServer(serviceId);
    }

    @Bean
    public ServerComponent serverComponent() {
        return new AmqpComponent(resultExchange, serviceId);
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory());
        container.setQueueNames(checkQueue);
        container.setMessageListener(messageListener());

        return container;
    }

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(host);
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
    public RabbitTemplate amqpTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setReceiveTimeout(listeningTimeout);
        template.setRoutingKey("test");
        template.setQueue(checkQueue);

        return template;
    }

    @Bean
    public MessageListener messageListener() {
        return serverComponent();
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

    @Bean
    public AmqpClient amqpClient() {
        return new AmqpClient();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new AvMessageConverter();
    }
}
