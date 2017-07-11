package dvoraka.avservice.server.configuration.jms;

import dvoraka.avservice.client.NetworkComponent;
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
 * JMS bridge output configuration for import.
 */
@Configuration
@Profile("to-jms")
public class JmsBridgeOutputConfig {

    @Value("${avservice.jms.brokerUrl}")
    private String brokerUrl;

    @Value("${avservice.jms.fileDestination}")
    private String fileDestination;
    @Value("${avservice.jms.resultDestination}")
    private String resultDestination;

    @Value("${avservice.jms.receiveTimeout:2000}")
    private long receiveTimeout;

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
            MessageConverter outMessageConverter
    ) {
        JmsTemplate template = new JmsTemplate(outConnectionFactory);
        template.setReceiveTimeout(receiveTimeout);
        template.setMessageConverter(outMessageConverter);

        return template;
    }

    @Bean
    public NetworkComponent outComponent(
            JmsTemplate outJmsTemplate,
            MessageInfoService messageInfoService
    ) {
        return new JmsComponent(fileDestination, serviceId, outJmsTemplate, messageInfoService);
    }

    @Bean
    public MessageListener outMessageListener(NetworkComponent outComponent) {
        return outComponent;
    }

    @Bean
    public SimpleMessageListenerContainer outMessageListenerContainer(
            ConnectionFactory outConnectionFactory,
            MessageListener outMessageListener
    ) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(outConnectionFactory);
        container.setDestinationName(resultDestination);
        container.setMessageListener(outMessageListener);

        return container;
    }
}
