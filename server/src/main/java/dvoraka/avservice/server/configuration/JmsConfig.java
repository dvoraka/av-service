package dvoraka.avservice.server.configuration;

import dvoraka.avservice.DefaultMessageProcessor;
import dvoraka.avservice.MessageProcessor;
import dvoraka.avservice.common.ReceivingType;
import dvoraka.avservice.configuration.ServiceConfig;
import dvoraka.avservice.server.AvServer;
import dvoraka.avservice.server.BasicAvServer;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.jms.JmsClient;
import dvoraka.avservice.server.jms.JmsComponent;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

/**
 * JMS Spring configuration.
 */
@Configuration
@Import({ServiceConfig.class})
@Profile("jms")
public class JmsConfig {

    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;

    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;


    @Bean
    public AvServer avServer() {
        return new BasicAvServer();
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);

        return factory;
    }

    @Bean
    public ServerComponent serverComponent() {
        return new JmsComponent();
    }

    @Bean
    public MessageProcessor messageProcessor() {
        final int threads = 20;

        return new DefaultMessageProcessor(threads, ReceivingType.LISTENER, 0);
    }

    @Bean
    @Profile("jms-async")
    public SimpleMessageListenerContainer messageListenerContainer() {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(activeMQConnFactory());
        container.setDestinationName(JmsClient.TEST_DESTINATION);
        container.setMessageListener(messageListener());

        return container;
    }

    @Bean
    public ConnectionFactory connectionFactory(ConnectionFactory activeMQConnFactory) {
        ConnectionFactory factory = new CachingConnectionFactory(activeMQConnFactory);

        return factory;
    }

    @Bean
    public JmsTemplate jmsTemplate(ConnectionFactory connectionFactory) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setReceiveTimeout(receiveTimeout);
        template.setMessageConverter(messageConverter());

        return template;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("typeId");

        return messageConverter;
    }

    @Bean
    public MessageListener messageListener() {
        return serverComponent();
    }

    @Bean
    public JmsClient jmsClient() {
        return new JmsClient();
    }
}
