package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.db.service.MessageInfoService;
import dvoraka.avservice.server.ServerComponent;
import dvoraka.avservice.server.jms.JmsComponent;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;

/**
 * JMS bridge input configuration for import.
 */
@Configuration
public class JmsBridgeInputConfig {

    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;

    @Value("${avservice.jms.checkDestination:check}")
    private String checkDestination;
    @Value("${avservice.jms.resultDestination:result}")
    private String resultDestination;

    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;

    @Value("${avservice.serviceId:default1")
    private String serviceId;


    @Bean
    public ServerComponent inComponent(
            JmsTemplate jmsTemplate,
            MessageInfoService messageInfoService) {
        return new JmsComponent(resultDestination, serviceId, jmsTemplate, messageInfoService);
    }

    @Bean
    public ConnectionFactory inActiveMQConnFactory() {
        return new ActiveMQConnectionFactory(brokerUrl);
    }

    @Bean
    public ConnectionFactory inConnectionFactory(ConnectionFactory inActiveMQConnFactory) {
        return new CachingConnectionFactory(inActiveMQConnFactory);
    }

    @Bean
    public SimpleMessageListenerContainer inMessageListenerContainer(
            ConnectionFactory inActiveMQConnFactory,
            MessageListener inMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(inActiveMQConnFactory);
        container.setDestinationName(checkDestination);
        container.setMessageListener(inMessageListener);

        return container;
    }

    @Bean
    public MessageConverter inMessageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("typeId");

        return messageConverter;
    }

    @Bean
    public JmsTemplate inJmsTemplate(
            ConnectionFactory inConnectionFactory,
            MessageConverter inMessageConverter) {
        JmsTemplate template = new JmsTemplate(inConnectionFactory);
        template.setReceiveTimeout(receiveTimeout);
        template.setMessageConverter(inMessageConverter);

        return template;
    }

    @Bean
    public MessageListener inMessageListener(ServerComponent inComponent) {
        return inComponent;
    }
}
