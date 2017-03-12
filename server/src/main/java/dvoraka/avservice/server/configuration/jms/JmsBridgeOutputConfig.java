package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.client.ServerComponent;
import dvoraka.avservice.client.jms.JmsComponent;
import dvoraka.avservice.db.service.MessageInfoService;
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
 * JMS bridge output configuration for import.
 */
@Configuration
public class JmsBridgeOutputConfig {

    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;

    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;

    @Value("${avservice.jms.resultDestination:result}")
    private String resultDestination;
    @Value("${avservice.jms.checkDestination:check}")
    private String checkDestination;

    @Value("${avservice.serviceId:default1}")
    private String serviceId;


    @Bean
    public ActiveMQConnectionFactory outActiveMQConnFactory() {
        return new ActiveMQConnectionFactory(brokerUrl);
    }

    @Bean
    public ConnectionFactory outConnectionFactory(ConnectionFactory outActiveMQConnFactory) {
        return new CachingConnectionFactory(outActiveMQConnFactory);
    }

    @Bean
    public MessageConverter outMessageConverter() {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("typeId");

        return messageConverter;
    }

    @Bean
    public JmsTemplate outJmsTemplate(
            ConnectionFactory outConnectionFactory,
            MessageConverter outMessageConverter) {
        JmsTemplate template = new JmsTemplate(outConnectionFactory);
        template.setReceiveTimeout(receiveTimeout);
        template.setMessageConverter(outMessageConverter);

        return template;
    }

    @Bean
    public ServerComponent outComponent(
            JmsTemplate outJmsTemplate,
            MessageInfoService messageInfoService
    ) {
        return new JmsComponent(checkDestination, serviceId, outJmsTemplate, messageInfoService);
    }

    @Bean
    public MessageListener outMessageListener(ServerComponent outComponent) {
        return outComponent;
    }

    @Bean
    public SimpleMessageListenerContainer outMessageListenerContainer(
            ActiveMQConnectionFactory outActiveMQConnectionFactory,
            MessageListener outMessageListener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(outActiveMQConnectionFactory);
        container.setDestinationName(resultDestination);
        container.setMessageListener(outMessageListener);

        return container;
    }
}
