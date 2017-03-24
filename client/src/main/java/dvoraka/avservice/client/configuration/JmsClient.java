package dvoraka.avservice.client.configuration;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.jms.JmsComponent;
import dvoraka.avservice.db.service.MessageInfoService;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

/**
 * JMS client configuration for the import.
 */
@Configuration
@Profile("jms-client")
public class JmsClient {

    @Value("${avservice.jms.resultDestination:result}")
    private String resultDestination;

    @Value("${avservice.jms.checkDestination:check}")
    private String checkDestination;

    @Value("${avservice.serviceId}")
    private String serviceId;

    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;

    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;

    @Bean
    public ServerComponent serverComponent(
            JmsTemplate jmsTemplate,
            MessageInfoService messageInfoService
    ) {
        return new JmsComponent(checkDestination, serviceId, jmsTemplate, messageInfoService);
    }

    @Bean
    public MessageListener messageListener(ServerComponent serverComponent) {
        return serverComponent;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(
            ActiveMQConnectionFactory activeMQConnectionFactory,
            MessageListener messageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(activeMQConnectionFactory);
        container.setDestinationName(resultDestination);
        container.setMessageListener(messageListener);

        return container;
    }

    @Bean
    public ActiveMQConnectionFactory activeMQConnFactory() {
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
        factory.setUseAsyncSend(true);

        return factory;
    }

    @Bean
    public ConnectionFactory connectionFactory(ConnectionFactory activeMQConnFactory) {
        return new CachingConnectionFactory(activeMQConnFactory);
    }

    @Bean
    public JmsTemplate jmsTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {
        JmsTemplate template = new JmsTemplate(connectionFactory);
        template.setReceiveTimeout(receiveTimeout);
        template.setMessageConverter(messageConverter);

        return template;
    }

    @Bean
    public MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("typeId");

        return messageConverter;
    }
}
